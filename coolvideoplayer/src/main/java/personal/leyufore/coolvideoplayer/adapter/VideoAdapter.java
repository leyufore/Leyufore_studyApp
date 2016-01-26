package personal.leyufore.coolvideoplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import personal.leyufore.coolvideoplayer.R;
import personal.leyufore.coolvideoplayer.model.VideoInfo;

/**
 * Created by wenrule on 16/1/25.
 */
public class VideoAdapter extends BaseAdapter{

    private ArrayList<VideoInfo> videolist;
    private LayoutInflater mInflater;

    public VideoAdapter(Context context,ArrayList<VideoInfo> videolist) {

        this.videolist = videolist;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return (videolist == null)? 0 : videolist.size();
    }

    @Override
    public Object getItem(int position) {
        return videolist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
 /*   其中BaseAdapter需要重写的方法:
    getCount(),getItem(int position),getItemId(int position),getView(int position, View convertView, ViewGroup parent)
    listView在开始绘制的时候，系统首先调用getCount()函数，根据他的返回值得到 listView的长度，然后根据这个长度，
    调用getView()逐一绘制每一行。如果你的 getCount()返回值是0的话，列表将不显示同样return 1，就只显示一行。
    系统显示列表时，首先实例化一个适配器（这里将实例化自定义的适配器）。当手动完成适配时，必须手动映射数据，这需要重写getView（）方法。
    系统在绘制列表的每一行的时候将调用此方法。getView()有三个参数，position表示将显示的是第几行，covertView是从布局文件中inflate来的布局。
    我们用LayoutInflater的方法将定义好的item.xml文件提取成View实例用来显示。然后将xml文件中 的各个组件实例化（简单的findViewById()方法）。
    这样便可以将数据对应到各个组件上了。但是按钮为了响应点击事件，需要为它添加点击监听 器，这样就能捕获点击事件。至此一个自定义的listView就完成了，
    现在让我们回过头从新审视这个过程。系统要绘制ListView了，他首先获得要 绘制的这个列表的长度，然后开始绘制第一行，怎么绘制呢？调用getView()函数。
    在这个函数里面首先获得一个View（实际上是一个 ViewGroup），然后再实例并设置各个组件，显示之。好了，绘制完这一行了。那再绘制下一行，直到绘完为止。
*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder videoHolder;
        if(null == convertView){
            convertView = mInflater.inflate(R.layout.list_item_videoinfo,null);

            videoHolder =  new ViewHolder();

            videoHolder.tvName = (TextView) convertView.findViewById(R.id.tv_video_name);
            videoHolder.tvSize = (TextView) convertView.findViewById(R.id.tv_video_size);
            videoHolder.ivThumbnail = (ImageView) convertView.findViewById(R.id.iv_video_thumbnail);

            convertView.setTag(videoHolder);
        }else{
            videoHolder = (ViewHolder) convertView.getTag();
        }

        VideoInfo video = videolist.get(position);
        videoHolder.tvName.setText(video.getName());
        videoHolder.tvSize.setText(video.getSize());
        videoHolder.ivThumbnail.setImageBitmap(video.getThumbnail());

        return convertView;
    }
    static class ViewHolder{
        ImageView ivThumbnail;
        TextView tvName;
        TextView tvSize;
    }
}
