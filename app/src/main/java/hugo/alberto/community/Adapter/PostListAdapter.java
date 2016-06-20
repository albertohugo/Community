package hugo.alberto.community.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import hugo.alberto.community.Model.CommunityPost;
import hugo.alberto.community.R;


public class PostListAdapter extends ParseQueryAdapter<CommunityPost> {
    private Integer[]images={R.mipmap.ic_happy,R.mipmap.ic_angel,R.mipmap.ic_confused_2,R.mipmap.ic_blablabla,R.mipmap.ic_blank_face,R.mipmap.ic_blushing,R.mipmap.ic_closing_eyes,R.mipmap.ic_confused,R.mipmap.ic_angry,R.mipmap.ic_cool,R.mipmap.ic_crying,R.mipmap.ic_crying_laugh,R.mipmap.ic_dancing,R.mipmap.ic_devil,R.mipmap.ic_disgust,R.mipmap.ic_dissapointed,R.mipmap.ic_dreaming,R.mipmap.ic_epic_win,R.mipmap.ic_eurica,R.mipmap.ic_evil_laugh,R.mipmap.ic_eye_roll,R.mipmap.ic_eyes_closed,R.mipmap.ic_eyes_hearts,R.mipmap.ic_forgot,R.mipmap.ic_hand_smack,R.mipmap.ic_hello,R.mipmap.ic_hug,R.mipmap.ic_drop,R.mipmap.ic_joke,R.mipmap.ic_kiss,R.mipmap.ic_like,R.mipmap.ic_like_2,R.mipmap.ic_lol,R.mipmap.ic_love,R.mipmap.ic_mad,R.mipmap.ic_nerd,R.mipmap.ic_overworked,R.mipmap.ic_poop,R.mipmap.ic_pour,R.mipmap.ic_rock,R.mipmap.ic_scared,R.mipmap.ic_secret,R.mipmap.ic_sleeping,R.mipmap.ic_success,R.mipmap.ic_wink};

    public PostListAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<CommunityPost>() {
                public ParseQuery create() {
                    ParseQuery query = new ParseQuery("Posts");
                    query.whereEqualTo("user", ParseUser.getCurrentUser());
                    return query;
                }
            });
    }

    @Override
    public View getItemView(final CommunityPost post, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(getContext(), R.layout.item_list_posts, null);
        }
        ImageView fellingImg = (ImageView) view.findViewById(R.id.felling);
        TextView post_text = (TextView) view.findViewById(R.id.post_txt);
        TextView location = (TextView) view.findViewById(R.id.location);
        TextView date = (TextView) view.findViewById(R.id.date_post);

        fellingImg.setImageResource(images[Integer.parseInt(post.getFeeling())]);

        if (post.getText().isEmpty()){
            post_text.setText("No text in this post.");
        }else{
            post_text.setText(post.getText());
        }

        date.setText(String.valueOf(post.getCreatedAt()));
        location.setText(post.getAddress());

        return view;
    }


}
