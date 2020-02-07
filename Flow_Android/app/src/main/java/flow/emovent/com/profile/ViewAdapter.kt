package flow.emovent.com.profile

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import flow.emovent.com.R
import flow.emovent.com.modules.buildListFromString
import flow.emovent.com.modules.decodeBitmapFromInputStream
import flow.emovent.com.modules.server
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder




class ViewAdapter(val dataSet:ArrayList<String>, val mCtx:Context): RecyclerView.Adapter<ViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v=LayoutInflater.from(p0.context).inflate(R.layout.profile_posts,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount():Int = dataSet.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val postInfo:JSONObject?=JSONObject(dataSet[p1])
        postInfo?.apply{
            p0.descriptionView.text = postInfo.getString("description")
            p0.guestsView.text = postInfo.getString("guests")
            p0.roomView.text = postInfo.getString("room")
            p0.bedsView.text = postInfo.getString("beds")
            p0.titleView.text = postInfo.getString("title")
        }






        class GetImage(imageUrl:String):AsyncTask<Bitmap?,Bitmap?,Bitmap?>(){
            val url= URL("http://$server/getPhotos?name=${URLEncoder.encode(imageUrl,"UTF-8")}")
            override fun doInBackground(vararg params: Bitmap?): Bitmap? {
                return try{
                    with(url.openConnection() as HttpURLConnection){
                        requestMethod="POST"
                        doInput=true
                        if(responseCode==HttpURLConnection.HTTP_OK) decodeBitmapFromInputStream(inputStream)
                        else decodeBitmapFromInputStream(errorStream)
                    }
                }catch (e:Exception){ Log.e("Streaming",e.message);null}

            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)

                when (result) {
                    is Bitmap -> with(p0.imageView){
                        try{setImageBitmap(result)}
                        catch (e:Exception){
                            val id= postInfo!!["_id"]
                            Toast.makeText(mCtx,"Error in image, id:$id",Toast.LENGTH_LONG).show()
                            //soon send log to Emovent
                        }
                    }
                    else -> Toast.makeText(mCtx,"Error Loading image",Toast.LENGTH_LONG).show()
                }
            }
        }

        val imagesUrlList= buildListFromString(postInfo!!.getString("photos"))
        imagesUrlList.forEachIndexed { _, name ->
            GetImage(name).execute()
        }

        p0.expandView.setOnClickListener {
            if(p0.showMoreState.visibility==View.GONE){
                p0.showMoreState.visibility=View.VISIBLE
                p0.expandView.setImageResource(R.drawable.expand_less)
                p0.descriptionView.maxLines=Int.MAX_VALUE
            }else if(p0.showMoreState.visibility==View.VISIBLE){
                p0.showMoreState.visibility=View.GONE
                p0.expandView.setImageResource(R.drawable.expand_more)
                p0.descriptionView.maxLines=2
            }
        }
        p0.descriptionView.setOnClickListener { p0.expandView.performClick() }
        p0.moreOptions.setOnClickListener {
            val popup = PopupMenu(mCtx, p0.moreOptions)
            popup.apply{
                inflate(R.menu.navigation)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.save -> {
                            //handle save click
                            //save post
                            Toast.makeText(mCtx,"Save",Toast.LENGTH_LONG).show()
                            true
                        }
                        R.id.hide ->{
                            //handle hide click
                            //hide post
                            Toast.makeText(mCtx,"Save",Toast.LENGTH_LONG).show()
                            true
                        }
                        R.id.report ->{
                            //handle report click
                            //report post
                            true
                        }
                        else -> false
                    }
                }
                show()
            }


        }
    }

    class ViewHolder(view:View): RecyclerView.ViewHolder(view){
        val showMoreState= view.findViewById<LinearLayout>(R.id.showMore)!!
        val expandView=view.findViewById<ImageView>(R.id.more_less)!!
        val descriptionView=view.findViewById<TextView>(R.id.description)!!
        val moreOptions=view.findViewById<ImageView>(R.id.more_options)!!
        val imageView=view.findViewById<ImageView>(R.id.imageView)!!
        val guestsView=view.findViewById<TextView>(R.id.guests)!!
        val roomView=view.findViewById<TextView>(R.id.room)!!
        val bedsView=view.findViewById<TextView>(R.id.beds)!!
        val titleView=view.findViewById<TextView>(R.id.titleView)
    }


}