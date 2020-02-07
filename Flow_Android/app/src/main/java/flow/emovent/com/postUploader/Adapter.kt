package flow.emovent.com.postUploader

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import flow.emovent.com.R
import java.util.ArrayList

class Adapter(val arrayList: ArrayList<Any>) : RecyclerView.Adapter<Adapter.ViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(p0.context).inflate(R.layout.post_uploader,p0,false)
        )
    }

    override fun getItemCount(): Int = arrayList.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(view:View): RecyclerView.ViewHolder(view)
}