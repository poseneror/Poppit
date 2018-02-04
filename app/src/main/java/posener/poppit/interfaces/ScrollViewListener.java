package posener.poppit.interfaces;

import posener.poppit.views.ObservableScrollView;

/**
 * Created by Or on 20/07/2015.
 */
public interface ScrollViewListener {
    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
}