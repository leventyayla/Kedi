package tez.levent.feyyaz.kedi.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private boolean isError = false;
    private int visibleThreshold = 3; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if(dy > 0) //check for scroll down
        {
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if ((!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) || isError) {
                // End has been reached

                onLoadMore();

                loading = true;
                isError = false;
            }
        }
    }

    public void setIsError(Boolean isError){
        this.isError=isError;
    }

    public void reset(int visibleThreshold){
        previousTotal = 0;
        loading = true;
        this.visibleThreshold = visibleThreshold;
    }

    public abstract void onLoadMore();
}