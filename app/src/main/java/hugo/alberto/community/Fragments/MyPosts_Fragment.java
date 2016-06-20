package hugo.alberto.community.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import hugo.alberto.community.Activities.PostActivity;
import hugo.alberto.community.Adapter.PostListAdapter;
import hugo.alberto.community.Model.CommunityPost;
import hugo.alberto.community.R;

public class MyPosts_Fragment extends Fragment {

    public PostListAdapter postsListAdapter;
    private ParseQueryAdapter<CommunityPost> mainAdapter;
    ListView postsListView;
    private CommunityPost post;
    private String postId = null;
    private static final int EDIT_ACTIVITY_CODE = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.myposts_layout, container, false);

        mainAdapter = new ParseQueryAdapter<>(getActivity(), CommunityPost.class);
        mainAdapter.setTextKey("title");
        mainAdapter.setImageKey("photo");

        postsListAdapter = new PostListAdapter(getActivity());

        postsListView = (ListView) rootView.findViewById(R.id.posts_listview);

        postsListAdapter.loadObjects();
        postsListView.setAdapter(postsListAdapter);

        registerForContextMenu(postsListView);

        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,final View view,
                                   final int position, final long id) {
                post = postsListAdapter.getItem(position);
                openEditView(post);

            }
        });

        postsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,final View view,
                                          final int position, final long id) {

                final int pos = position;
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Delete");
                alertDialog.setMessage("Do you really want to delete this post?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                post = postsListAdapter.getItem(pos);
                                postId = post.getUuidString();
                                delete(postId);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            }
        });

        return rootView;
    }

    private void openEditView(CommunityPost post) {
        Intent i = new Intent(getContext(), PostActivity.class);
        i.putExtra("ID", post.getUuidString());
        startActivityForResult(i, EDIT_ACTIVITY_CODE);
    }

    private void delete(String todoId) {
        ParseQuery<CommunityPost> query = CommunityPost.getQuery();
        query.whereEqualTo("uuid", todoId);
        query.getFirstInBackground(new GetCallback<CommunityPost>() {

            @Override
            public void done(CommunityPost object, ParseException e) {
                object.deleteEventually();
                postsListAdapter.loadObjects();
                postsListView.setAdapter(postsListAdapter);
            }

        });
    }
}