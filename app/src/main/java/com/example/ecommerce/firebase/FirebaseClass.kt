package com.example.ecommerce.firebase

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.ecommerce.activities.AddEditAddressActivity
import com.example.ecommerce.activities.AddProductActivity
import com.example.ecommerce.activities.AddressListActivity
import com.example.ecommerce.activities.CartListActivity
import com.example.ecommerce.model.User
import com.example.ecommerce.activities.LoginActivity
import com.example.ecommerce.activities.ProductsDetailsActivity
import com.example.ecommerce.activities.RegisterActivity
import com.example.ecommerce.activities.SettingsActivity
import com.example.ecommerce.activities.UserProfileActivity
import com.example.ecommerce.fragments.DashboardFragment
import com.example.ecommerce.fragments.ProductsFragment
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.CartItem
import com.example.ecommerce.model.Product
import com.example.ecommerce.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class FirebaseClass {
    val databaseUser = FirebaseDatabase.getInstance().getReference(Constants.USERS)
    val databaseProduct = FirebaseDatabase.getInstance().getReference(Constants.PRODUCTS)
    val databaseCartItems = FirebaseDatabase.getInstance().getReference(Constants.CART_ITEMS)
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        databaseUser.child(userInfo.uid.toString()).setValue(userInfo).addOnSuccessListener {
            activity.userRegistrationSuccess()
        }.addOnFailureListener {
            activity.hideProgressDialog()
        }
    }

    fun getCurrentUserId(): String {
        val currentUser = firebaseAuth.currentUser

        var currentUserId = ""

        if (currentUser != null) {
            currentUserId = currentUser.uid
        }

        return currentUserId
    }

    fun getUserDetails(activity: Activity) {
        databaseUser.child(getCurrentUserId()).get().addOnSuccessListener {
//            override fun onDataChange(snapshot: DataSnapshot) {

//                if (snapshot.exists()) {
                    val user = it.getValue(User::class.java)
                    val sharedPreferences = activity.getSharedPreferences(
                        Constants.ECOMM_PREFERENCES,
                        Context.MODE_PRIVATE
                    )
                    val editor = sharedPreferences.edit()

                    editor.putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user!!.firstName} ${user.lastName}"
                    ).apply()

                    when (activity) {
                        is LoginActivity -> {
                            activity.userLoggedInSuccess(user)
                        }
                        is SettingsActivity ->{
                            activity.userDataFetchSuccess(user)
                        }
                    }
                }
//            }

//            override fun onCancelled(error: DatabaseError) {
//                // Handle error
////                fragment.hideProgressDialog()
//            }
//        })
    }

    fun updateUserDetails(
        activity: Activity,
        userHashMap: HashMap<String, Any>,
        profileCompleted: Int
    ) {
        databaseUser.child(getCurrentUserId()).updateChildren(userHashMap).addOnSuccessListener {
            when (activity) {
                is UserProfileActivity -> {
                    activity.userProfileUpdateSuccess(profileCompleted)
                }
            }
        }.addOnFailureListener {
            when (activity) {
                is UserProfileActivity -> {
                    activity.hideProgressDialog()
                }
            }
        }
    }

    fun uploadImageToFirebaseStorage(activity: Activity, imageFileUri: Uri, profileCompleted: Int?, imageType : String) {


        val storageRef = storage.reference.child(
            imageType + System.currentTimeMillis()
                    + "." + Constants.getFileExtension(activity, imageFileUri)
        )

        storageRef.putFile(imageFileUri).addOnSuccessListener {
            it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                Log.d("imgUriFromFirebase", it.toString())

                when (activity) {
                    is UserProfileActivity -> {
                        activity.imageUploadSuccess(it.toString(), profileCompleted!!)
                    }

                    is AddProductActivity -> {
                        activity.imageUploadSuccess(it.toString(), null)
                    }
                }
            }
        }.addOnFailureListener {
            when (activity) {
                is UserProfileActivity -> {
                    activity.hideProgressDialog()
                }
            }
        }
    }

    fun logOutCurrentUser(activity: Activity) {
        when (activity) {
            is SettingsActivity -> {
                firebaseAuth.signOut()
                activity.logoutSuccess()
            }
        }
    }

    fun uploadProductDetails(
        activity: AddProductActivity,
        product: Product,
        dbRef: DatabaseReference
    ) {
        dbRef.setValue(product).addOnSuccessListener {
            activity.productDetailsUploadedSuccessfully()
        }.addOnFailureListener {
            activity.hideProgressDialog()
        }
    }

    fun getProductDetails(fragment: ProductsFragment) : ArrayList<Product>{
        val productList = ArrayList<Product>()

        databaseProduct.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                productList.clear()

                if (snapshot.exists()){
                    for (productSnapshot in snapshot.children) {

                        val product = productSnapshot.getValue(Product::class.java)

                        if (product != null && product.uid == getCurrentUserId()) {
                            productList.add(product)

                        }
                    }
                    fragment.productsDetailsFetchSuccess(productList)

                }
                else{
                    fragment.hideProgressDialog()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                fragment.hideProgressDialog()
            }

        })
        return productList
    }

    fun deleteProductFromFirebase(fragment: Fragment, productId: String?) {
        databaseProduct.child(productId!!).removeValue().addOnSuccessListener {


            // Reference to the node where cart items are stored for all users
            val dbAllUsersCarts = databaseUser.parent!!.child(Constants.USERS)

            // Iterate over all users' carts
            dbAllUsersCarts.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        // Reference to the cart node of each user
                        val userCartRef = userSnapshot.child(Constants.CART_ITEMS)

                        // Check if the user has any cart items
                        if (userCartRef.exists()) {
                            // Iterate over the cart items of the user
                            userCartRef.children.forEach { cartItemSnapshot ->
                                val cartItem = cartItemSnapshot.getValue(CartItem::class.java)

                                // Check if the cart item corresponds to the deleted product
                                if (cartItem != null && cartItem.productId == productId) {
                                    // Remove the cart item
                                    cartItemSnapshot.ref.removeValue()
                                }
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
//                    activity.errorGettingAllCartListItems()
                }
            })


                when(fragment){
                is ProductsFragment ->{
                    fragment.productDeletedSuccess()
                }
            }
        }
    }

    fun getAllDashboardProducts(fragment: DashboardFragment) : ArrayList<Product>{
        val allProductList = ArrayList<Product>()

        databaseProduct.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                allProductList.clear()

                if (snapshot.exists()){
                    for (productSnapshot in snapshot.children) {

                        val product = productSnapshot.getValue(Product::class.java)

                        if (product != null && product.uid != getCurrentUserId()) {
                            allProductList.add(product)

                        }
                    }
                    fragment.allDashboardProductsFetchSuccess(allProductList)

                }
                else{
                    fragment.errorGettingAllDashboardProducts()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                fragment.errorGettingAllDashboardProducts()
            }

        })
        return allProductList
    }

    fun addCartItems(activity: ProductsDetailsActivity, addToCart : CartItem){
        val productId = addToCart.productId
        val totalQuantity = addToCart.productQuantity
        val carItemQuantity = addToCart.cartQuantity

        val dbCart = databaseUser.child(getCurrentUserId()).child(Constants.CART_ITEMS)

        dbCart.child(productId!!).get().addOnSuccessListener {
            Log.d("valuee", it.value.toString())

            if (it.value == null){
                dbCart.child(addToCart.productId.toString()).setValue(addToCart).addOnSuccessListener {
                        val productHashMap = HashMap<String, Any>()

                    productHashMap[Constants.PRODUCT_QUANTITY] = totalQuantity!! - carItemQuantity!!

                    activity.productAddedToCartSuccess()
                    }.addOnFailureListener {
                        activity.hideProgressDialog()
                    }
                }
            else{
                dbCart.child(addToCart.productId.toString()).child(Constants.CART_QUANTITY).get().addOnSuccessListener {
                    val currentQuantity = it.value.toString().toInt()
                    val newQuantity = addToCart.cartQuantity

                    activity.showAlertDialog(currentQuantity, newQuantity, addToCart)
                }

            }
        }
    }

    fun updateCartItems(
        activity: ProductsDetailsActivity,
        addToCart: CartItem,
        currentQuantity: Int,
        newQuantity: Int?
    ) {

        val dbCart = databaseUser.child(getCurrentUserId()).child(Constants.CART_ITEMS)

        dbCart.child(addToCart.productId.toString()).child(Constants.CART_QUANTITY)
            .setValue(currentQuantity + addToCart.cartQuantity!!).addOnSuccessListener {
                activity.productAddedToCartSuccess()
            }.addOnFailureListener {
                activity.hideProgressDialog()
            }
    }

    fun getCartList(activity: CartListActivity) {
        val allCartListItems = ArrayList<CartItem>()


        val dbCart = databaseUser.child(getCurrentUserId()).child(Constants.CART_ITEMS)
        dbCart.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                allCartListItems.clear()

                if (snapshot.exists()){
                    for (cartListSnapshot in snapshot.children) {

                        val cartItem = cartListSnapshot.getValue(CartItem::class.java)

                        if (cartItem != null && cartItem.uid != getCurrentUserId()) {
                            allCartListItems.add(cartItem)
                        }
                    }
                    activity.allCartListItemsFetchSuccess(allCartListItems)

                }
                else{
                    activity.errorGettingAllCartListItems()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                activity.errorGettingAllCartListItems()
            }

        })
    }

    fun updateCartProductQuantityAndPrice(
        activity: Activity,
        cartProductHashMap: HashMap<String, Any>,
        productId: String?
    ) {

        val dbCart = databaseUser.child(getCurrentUserId()).child(Constants.CART_ITEMS)
        dbCart.child(productId.toString()).updateChildren(cartProductHashMap).addOnSuccessListener {
            when(activity){
                is CartListActivity ->{
                    activity.updateCartProductQuantityAndPriceSuccess()
                }
            }
        }
//            .setValue(currentQuantity + addToCart.productQuantity!!).addOnSuccessListener {
//                activity.productAddedToCartSuccess()
//            }.addOnFailureListener {
//                activity.hideProgressDialog()
//            }
    }

    fun deleteCartItemFromFirebase(activity: Activity, productId: String?, position: Int) {

        val dbCart = databaseUser.child(getCurrentUserId()).child(Constants.CART_ITEMS)
        dbCart.child(productId!!).removeValue().addOnSuccessListener {
            when(activity){
                is CartListActivity ->{
                    activity.cartItemDeletedSuccess(position)
                }
            }
        }
    }

    fun updateProductDetails(
        activity: ProductsDetailsActivity,
        productHashMap: HashMap<String, Any>,
        productDetails: Product
    ) {
        val productId = productDetails.productId

        databaseProduct.child(productId!!).updateChildren(productHashMap).addOnSuccessListener {
            activity.productDetailsUpdatedSuccess()
        }
    }

    fun addAddress(activity: AddEditAddressActivity, addressData: Address, dbRef: DatabaseReference) {
//        val dbAddress = databaseUser.child(getCurrentUserId()).child(Constants.ADDRESSES)

        dbRef.setValue(addressData).addOnSuccessListener {
            activity.addAddressSuccess()
        }
            .addOnFailureListener {
                activity.addAddressFailure()
            }
    }

    fun getAllAddressDetails(activity: AddressListActivity) {
        val allAddressListItems = ArrayList<Address>()

        val dbAddress = databaseUser.child(getCurrentUserId()).child(Constants.ADDRESSES)

        dbAddress.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                allAddressListItems.clear()

                if (snapshot.exists()){
                    for (addressSnapshot in snapshot.children) {
                        val address = addressSnapshot.getValue(Address::class.java)

                        if (address != null) {
                            allAddressListItems.add(address)
                        }
                    }
                    activity.addressListDataFetchSuccess(allAddressListItems)
                }
                else{
                    activity.noDataExistsInDatabase()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun updateAddress(
        activity: AddEditAddressActivity,
        addressHashMap: HashMap<String, Any>,
        addressId: String?
    ) {
        val dbAddress = databaseUser.child(getCurrentUserId()).child(Constants.ADDRESSES)

        dbAddress.child(addressId!!).updateChildren(addressHashMap).addOnSuccessListener {
            activity.addAddressSuccess()
        }
            .addOnFailureListener {
                activity.addAddressFailure()
            }

    }

    fun deleteAddressItemFromFirebase(activity: AddressListActivity, addressId: String?) {
        val dbAddress = databaseUser.child(getCurrentUserId()).child(Constants.ADDRESSES)

        dbAddress.child(addressId!!).removeValue().addOnSuccessListener {
            activity.deleteAddressSuccess()
        }.addOnFailureListener {
            activity.deleteAddressFailed()
        }
    }
}