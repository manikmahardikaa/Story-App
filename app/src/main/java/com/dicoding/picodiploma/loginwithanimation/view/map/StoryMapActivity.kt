package com.dicoding.picodiploma.loginwithanimation.view.map

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.ResultState
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStoryMapBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailStoryActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class StoryMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel by viewModels<StoryMapViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var listData = mutableListOf<ListStoryItem>()

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapBinding
    private val boundsBuilder = LatLngBounds.Builder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMapStyle()

        viewModel.getStoryWithLocation().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showLoading(false)

                        result.data.forEach { data ->
                            Log.d(
                                "Location: ",
                                "${data.name} ${data.description} Location: lat:${data.lat}  lon:${data.lon}"
                            )
                            listData.add(data)

                            val latLng = LatLng(data.lat!!.toDouble(), data.lon!!.toDouble())
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(data.name)
                                    .snippet(data.description)
                            ).also { marker ->
                                marker?.tag = data.id

                            }

                            boundsBuilder.include(latLng)
                        }

                        val bounds: LatLngBounds = boundsBuilder.build()
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                resources.displayMetrics.widthPixels,
                                resources.displayMetrics.heightPixels,
                                300
                            )
                        )

                        Toast.makeText(this, "Success to Load", Toast.LENGTH_SHORT).show()
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        Toast.makeText(this, "Failed to Load", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        mMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            if(marker != null){
                binding.btnDetailStory.visibility = View.VISIBLE

                binding.btnDetailStory.setOnClickListener{
                    val moveStoryDataIntent = Intent(this, DetailStoryActivity::class.java)
                    moveStoryDataIntent.putExtra(DetailStoryActivity.ID, marker.tag.toString())
                    this.startActivity(moveStoryDataIntent)
                }
            }
            true
        }

        mMap.setOnMapClickListener {
            binding.btnDetailStory.visibility = View.GONE
        }




    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }

    }

    fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "StoryMapActivity"
    }
}