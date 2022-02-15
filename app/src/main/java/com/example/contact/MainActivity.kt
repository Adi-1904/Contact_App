package com.example.contact

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contact.databinding.ActivityMainBinding
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {


    companion object {
        private const val REQUEST_READ_CONTACTS_CODE = 111
        private const val REQUEST_CALL=1
        private const val sms=2
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ContactListRecyclerViewAdapter
    private val list: ArrayList<ContactsModel> = ArrayList<ContactsModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRecyclerView()
        requestContactPermission()
        requestcallpermission()
        requestsmsPermission()


    }



    private fun setUpRecyclerView() {
        adapter = ContactListRecyclerViewAdapter(list,this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    @SuppressLint("Range")
    private fun getContacts() {


        var contactId = ""
        var displayName = ""

        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (cursor != null) {
            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {
                    val hasPhoneNumber: Int =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                            .toInt()
                    if (hasPhoneNumber > 0) {
                        contactId =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                        displayName =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        val phoneCursor: Cursor? = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(contactId),
                            null
                        )


                        if (phoneCursor?.moveToNext() == true) {
                            val phoneNumber: String =
                                phoneCursor.getString(
                                    phoneCursor.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER
                                    )
                                )


                            list.add(ContactsModel(displayName,phoneNumber))
                        }
                        phoneCursor?.close()
                    }
                }
            }

        }
        cursor?.close()

        adapter.notifyDataSetChanged()

    }

    fun requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.READ_CONTACTS
                    )
                ) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("Read Contacts permission")
                    builder.setPositiveButton("OK", null)
                    builder.setMessage("Please enable access to contacts.")
                    builder.setOnDismissListener(DialogInterface.OnDismissListener {
                        requestPermissions(
                            arrayOf(
                                android.Manifest.permission.READ_CONTACTS
                            ), REQUEST_READ_CONTACTS_CODE
                        )
                    })
                    builder.show()

                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(android.Manifest.permission.READ_CONTACTS),
                        REQUEST_READ_CONTACTS_CODE
                    )
                }
            } else {
                getContacts()
            }
        } else {
            getContacts()
        }
    }
    private fun requestcallpermission() {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.CALL_PHONE),REQUEST_CALL)
        }

    }

    private fun requestsmsPermission() {
  if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED)
  {
      ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), sms)
  }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS_CODE && grantResults.contains(PermissionChecker.PERMISSION_GRANTED)){
            getContacts()
        }
    }



}