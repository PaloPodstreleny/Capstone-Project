package com.artapp.podstreleny.palo.artist.ui.art.artworks;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.paging.PagedList;

import com.artapp.podstreleny.palo.artist.db.entity.Artwork;
import com.artapp.podstreleny.palo.artist.network.Resource;
import com.artapp.podstreleny.palo.artist.network.Status;
import com.artapp.podstreleny.palo.artist.repositories.artworks.ArtworkRepository;
import com.artapp.podstreleny.palo.artist.utils.ArtysToken;

public class ArtworksViewModel extends AndroidViewModel {

    private ArtworkRepository mRepository = ArtworkRepository.getInstance(getApplication());

    private MutableLiveData<Boolean> shouldFetchToken = new MutableLiveData<>();
    private LiveData<Resource<ArtysToken>> token = Transformations.switchMap(shouldFetchToken, new Function<Boolean, LiveData<Resource<ArtysToken>>>() {
        @Override
        public LiveData<Resource<ArtysToken>> apply(Boolean input) {
            return mRepository.fetchToken();
        }
    });

    private MutableLiveData<String> tokenChanged = new MutableLiveData<>();
    private LiveData<PagedList<Artwork>> artworks = Transformations.switchMap(tokenChanged, new Function<String, LiveData<PagedList<Artwork>>>() {
        @Override
        public LiveData<PagedList<Artwork>> apply(String input) {
            return mRepository.getArtworks(input);
        }
    });

    private LiveData<Status> status = Transformations.switchMap(tokenChanged, new Function<String, LiveData<Status>>() {
        @Override
        public LiveData<Status> apply(String input) {
            return mRepository.getActualStatus();
        }
    });

    public ArtworksViewModel(Application application){
        super(application);
    }

    public LiveData<PagedList<Artwork>> getArtworks() {
        return artworks;
    }

    public LiveData<Resource<ArtysToken>> getToken() {
        return token;
    }

    public LiveData<Status> getStatus(){return status;}

    public void fetchToken(){
        final Boolean value = shouldFetchToken.getValue();
        if(value == null){
            shouldFetchToken.setValue(true);
        }else {
            shouldFetchToken.setValue(!value);
        }
    }

    public void setToken(String token){
        tokenChanged.setValue(token);
    }


}
