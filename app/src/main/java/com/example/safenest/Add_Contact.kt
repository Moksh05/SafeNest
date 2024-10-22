package com.example.safenest

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.safenest.adapters.contactlistAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton


class Add_Contact : Fragment() {
    private val REQUEST_CONTACTS_PERMISSION = 1
    private lateinit var adapter2 : contactlistAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddNumber: FloatingActionButton
    private val phoneNumberList = mutableListOf<Pair<String, String>>()
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var nameInput: EditText? = null
    private var numberInput: EditText? = null
    val CONTACT_PICKER_REQUEST = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getallcontacts()
        recyclerView = view.findViewById(R.id.contact_list)
        fabAddNumber = view.findViewById(R.id.add_contact)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter2 = contactlistAdapter(phoneNumberList)
        recyclerView.adapter = adapter2

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Get the position of the item that was swiped
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Remove the contact from the list
                    val contact = phoneNumberList[position]
                    phoneNumberList.removeAt(position)
                    adapter2.notifyItemRemoved(position)
                    // Optionally, show a Toast or Snackbar to confirm deletion
                    Toast.makeText(requireContext(), "Deleted: ${contact.first}", Toast.LENGTH_SHORT).show()

                    // Remove from SharedPreferences if needed
                    val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.remove("saved_phone_number_${contact.first}")
                    editor.apply()
                }
            }

            // Optional: Add swipe background color
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView
                    val background = ColorDrawable(Color.RED)
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
        })

        // Attach the ItemTouchHelper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView)


        fabAddNumber.setOnClickListener { showbottomdialog() }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun checkContactPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACTS_PERMISSION)
        } else {
            pickContact()
        }
    }

    private fun showbottomdialog(){
        bottomSheetDialog = BottomSheetDialog(requireContext())
        val dialog = layoutInflater.inflate(R.layout.add_contact_dialog,null)
        bottomSheetDialog!!.setContentView(dialog)

        nameInput = dialog.findViewById<EditText>(R.id.input_name)
        numberInput = dialog.findViewById<EditText>(R.id.input_phone_number)
        val importContactButton = dialog.findViewById<Button>(R.id.btn_import_contact)
        val saveButton = dialog.findViewById<Button>(R.id.btn_save)

        importContactButton.setOnClickListener {
            checkContactPermission()
        }

        saveButton.setOnClickListener {
            val name = nameInput?.text.toString()
            val number = "+91" + numberInput?.text.toString()

            if (name.isNotEmpty() && number.isNotEmpty()) {
                savePhoneNumber(name, number)
                bottomSheetDialog!!.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter both name and number", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetDialog!!.show()
    }

    private fun savePhoneNumber(name : String , number : String){
        phoneNumberList.add(Pair(name, number))
        recyclerView.adapter?.notifyDataSetChanged()

        // Save to SharedPreferences if needed
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("saved_phone_number_$name", "+91$number")
        editor.apply()
    }


    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, CONTACT_PICKER_REQUEST)
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CONTACT_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            val contactUri: Uri? = data?.data
            contactUri?.let { uri ->
                // Query the contact ID and details
                val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                        val hasPhoneNumber = it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                        if (hasPhoneNumber > 0) {
                            val phoneCursor = requireContext().contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                                arrayOf(id),
                                null
                            )
                            phoneCursor?.use { pc ->
                                if (pc.moveToFirst()) {
                                    val phoneNumber = pc.getString(pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    // Do something with the phone number (e.g., display or store it)
                                    Log.d("PhoneNumber", "Selected phone number: $phoneNumber")
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    fun getallcontacts(){
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all

        // Iterate through all entries in SharedPreferences
        for ((key, value) in allEntries) {
            if (key.startsWith("saved_phone_number_")) {
                val contactName = key.removePrefix("saved_phone_number_")
                val contactNumber = value.toString()
                // Log the contact name and number
                phoneNumberList.add(Pair(contactName,contactNumber))
                Log.d("StoredContact", "Name: $contactName, Number: $contactNumber")
            }
        }
    }





}