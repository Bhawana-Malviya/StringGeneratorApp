package com.example.randomstringgeneratorapp.data.provider

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.randomstringgeneratorapp.data.model.RandomStringData
import org.json.JSONObject
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


object RandomStringProviderHelper {

    private const val AUTHORITY = "com.iav.contestdataprovider"
    private const val DATA_URI = "content://$AUTHORITY/text"
    private const val COLUMN_NAME = "data"

    fun fetchRandomString(
        contentResolver: ContentResolver,
        maxLength: Int
    ): Result<RandomStringData> {
        return try {
            val uri = Uri.parse(DATA_URI)

            val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val queryArgs = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, maxLength)
                }
                contentResolver.query(uri, null, queryArgs, null)
            } else {
                // Fallback for API < 26: use sortOrder "LIMIT 1" if supported
                contentResolver.query(uri, null, null, null, "created DESC LIMIT 1")
            }
            cursor?.use {
                if (it.moveToFirst()) {
                    val jsonData = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME))
                    val jsonObject = JSONObject(jsonData)
                    val randomText = jsonObject.getJSONObject("randomText")

                    val value = randomText.getString("value")
                    val length = randomText.getInt("length")
                    val created = randomText.getString("created")

                    Result.success(
                        RandomStringData(
                            value = value,
                            length = length,
                            createdAt = date(created)
                        )
                    )
                } else {
                    Result.failure(Exception("Empty response from ContentProvider"))
                }
            } ?: Result.failure(Exception("Cursor is null"))
        } catch (e: Exception) {
            Log.e("ProviderHelper", "Error fetching string", e)
            Result.failure(e)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun date(created: String): String {
    return try {
        val parsedTime = ZonedDateTime.parse(created, DateTimeFormatter.ISO_ZONED_DATE_TIME)

        // Format to "dd-MM-yyyy hh:mm:ss"
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")

        // Return the formatted string
        parsedTime.format(formatter)
    } catch (e: Exception) {
        e.printStackTrace()
        created // fallback to raw string if parsing fails
    }
}