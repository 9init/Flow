package flow.emovent.com.modules

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import java.lang.StringBuilder
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.InputStream
import java.lang.Exception

const val server="192.168.1.7:8080"

interface PostTaskListener<K> {
    // K is the type of the result object of the async task
    fun onPostTask(result: K)
}
/*class uplaoder(var file: String): AsyncTask<String, String, String>(){
    override fun doInBackground(vararg p0: String?): String {
        // Connect to the web server endpoint
        try {
            val serverUrl = URL("http://$server/upload")
            val boundaryString = "----SomeRandomText"
            val logFileToUpload = File(file)
            with(serverUrl.openConnection() as HttpURLConnection){
                // Indicate that we want to write to the HTTP request body
                doOutput=true
                requestMethod = "POST"
                addRequestProperty("Content-Type", "multipart/form-data; boundary=$boundaryString")
                val outputStreamToRequestBody=outputStream
                val httpRequestBodyWriter = BufferedWriter(OutputStreamWriter(outputStreamToRequestBody))

                // Include value from the myFileDescription text area in the post data
                httpRequestBodyWriter.write("\n\n--$boundaryString\n")
                httpRequestBodyWriter.write("Content-Disposition: form-data; name=\"myFileDescription\"")

                // Include the section to describe the file
                httpRequestBodyWriter.write("\n--$boundaryString\n")
                httpRequestBodyWriter.write("Content-Disposition: form-data;"
                        + "name=\"myFile\";"
                        + "filename=\"" + logFileToUpload.name + "\""
                        + "\nContent-Type: text/plain\n\n")
                httpRequestBodyWriter.flush()

                // Write the actual file contents
                val inputStreamToLogFile = FileInputStream(logFileToUpload)

                val bytesRead: Int
                val dataBuffer = ByteArray(1024)
                bytesRead=inputStreamToLogFile.read(dataBuffer)
                while (bytesRead != -1) {
                    outputStreamToRequestBody.write(dataBuffer, 0, bytesRead)
                }

                outputStreamToRequestBody.flush()

                // Mark the end of the multipart http request
                httpRequestBodyWriter.write("\n--$boundaryString--\n")
                httpRequestBodyWriter.flush()

                // Close the streams
                outputStreamToRequestBody.close()
                httpRequestBodyWriter.close()

                //get response
                return if(errorStream==null)inputStream.bufferedReader().readLine()
                else errorStream.bufferedReader().readLine()
            }
        }catch (e:Exception){
            Log.e("./connectionError",e.toString())
            return e.cause.toString()
        }

    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        when(result){
            null -> {}//Toast.makeText(this@ProfileService,"something went wrong", Toast.LENGTH_LONG).show()
            else -> {}//Toast.makeText(this@ProfileService,result, Toast.LENGTH_LONG).show()
        }
    }
}*/

class InputFilterMinMax : InputFilter {
    private var min: Int = 0
    private var max: Int = 0
    constructor(min: Int, max: Int) {
        this.min = min
        this.max = max
    }
    constructor(min: String, max: String) {
        this.min = Integer.parseInt(min)
        this.max = Integer.parseInt(max)
    }
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        try {
            val input = Integer.parseInt(dest.toString() + source.toString())
            if (isInRange(min, max, input))
                return null
        } catch (nfe: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c in a..b else c in b..a
    }

    //EditText et = (EditText) findViewById(R.id.myEditText);
    //et.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "12")});
}




fun splitQueriesList(queries:StringBuilder):ArrayList<String>{
    queries.apply{
        deleteCharAt(0)
        deleteCharAt(queries.lastIndex)
    }
    val queriesList=ArrayList<String>()
    while(queries.isNotEmpty()){
        val queryEnd=queries.indexOf("}")
        val query=queries.substring(0,queryEnd+1)
        queries.delete(0,queryEnd+2)
        queriesList.add(query)
    }
    return queriesList
}

fun buildListFromString(string:String):MutableList<String>{
    val strBuilder=StringBuilder(string)
    strBuilder.apply{
        deleteCharAt(0)
        deleteCharAt(strBuilder.lastIndex)
    }
    return strBuilder.split(',').toMutableList()
}

fun decodeBitmapFromInputStream(inputStream: InputStream,sampleSize:Int=4): Bitmap? {
    BitmapFactory.Options().apply{
        return try {
            inSampleSize=sampleSize
            BitmapFactory.decodeStream(inputStream,null,this)
        }catch (e: Exception){
            decodeBitmapFromInputStream(inputStream,sampleSize+1)
        }
    }
}


fun getRealPath(contentUri: Uri, context: Context):String?{
    var path:String?= null
    val proj = arrayOf(MediaStore.MediaColumns.DATA)
    val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
    if (cursor!!.moveToFirst()) {
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        path = cursor.getString(column_index)
    }
    cursor.close()
    return path
}