package com.example.starbucks_mock_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.mobile.auth.core.IdentityHandler
import com.amazonaws.mobile.auth.core.IdentityManager
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var credentialsProvider: AWSCredentialsProvider? = null
    private var awsConfiguration: AWSConfiguration? = null
    //in the actual app there would probably be some kind of data structure holding each of the items along with their captions
    //this is just for demonstration purposes
    private val carouselMessages = arrayOf(
        "You could win a trip to Costa Rica. Join Starbucks® Rewards.",
        "Signing up is easy and fast.",
        "Exclusive offers, personalized for you",
        "Rewards in as few as 2-3 visits",
        "That means free drinks, food and more \uD83D\uDE4C"
    )

    companion object {
        var dynamoDBMapper: DynamoDBMapper? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AWSMobileClient.getInstance().initialize(this) {
            credentialsProvider = AWSMobileClient.getInstance().credentialsProvider
            awsConfiguration = AWSMobileClient.getInstance().configuration

            IdentityManager.getDefaultIdentityManager().getUserID(object : IdentityHandler {
                override fun handleError(exception: Exception?) {
                    Log.e("AWSMobileClientLog", "Retrieving identity: ${exception?.message}")
                }

                override fun onIdentityId(identityId: String?) {
                    Log.i("AWSMobileClientLog", "Successfully established identity")
                    //future work would be storing this locally
                    //val cachedIdentityId = IdentityManager.getDefaultIdentityManager().cachedUserID

                    val client = AmazonDynamoDBClient(AWSMobileClient.getInstance().credentialsProvider)
                    dynamoDBMapper = DynamoDBMapper.builder()
                        .dynamoDBClient(client)
                        .awsConfiguration(AWSMobileClient.getInstance().configuration)
                        .build()
                }
            })
        }.execute()

        horizontalScrollView.setOnScrollChangeListener { _, newX, _, _, _ ->
            //onScrollChange
            
            updateCarouselText(newX)
        }
        imageCaption.text = carouselMessages[0]
    }

    private fun updateCarouselText(x: Int = 0) {
        //little bit of tuning to be able to show the last caption...
        val carouselWidth = carousel.width - 100

        //on the actual app the carousel data is probably dynamically loaded, and the data is probably stored in some way with the
        //image and caption together
        val numItems = carouselMessages.size
        val carouselIndex = (x.toFloat()/carouselWidth.toFloat() * numItems).toInt()

        imageCaption.text = carouselMessages[carouselIndex]
    }

    fun goToLogin(view: View) {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    fun goToJoinNow(view: View) {
        val intent = Intent(this, JoinNowActivity::class.java)
        startActivity(intent)
    }
}
