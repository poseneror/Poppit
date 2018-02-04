package posener.poppit.objects;


import android.content.Context;
import posener.poppit.R;

/**
 * Created by Or on 30/07/2015.
 */
public class Filler {
            public int type = 0;
            public int layout;
            private Context myContext;
            public static final int TYPE_DIVIDER = 0;
            public static final int TYPE_INFLATER = 1;
            public static final int TYPE_PROFILE = 2;
            public static final int TYPE_COMMENT = 3;
            public static final int TYPE_PAGER = 4;

            public Filler(int type) {
                this.type = type;
                switch (type){
                    case TYPE_PROFILE:
                        SetMyProfile();
                        break;
                }
    }

    private void SetMyProfile(){
        layout = R.layout.fragment_profile;
    }

}

