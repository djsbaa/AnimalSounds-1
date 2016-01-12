package martinmo.dyrelyder;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private String currentAnimal;
    private ArrayList<String> previousAnimals = new ArrayList<String>();

    private static final int IMAGEWIDTH = 850;
    private static final int IMAGEHEIGHT = 760;
    private static final int FADEOUTDURATION = 250;
    private static final int NUMBER_OF_IMAGEVIEWS = 4;
    private static final String[] ANIMALS =
            {
                    "dog",
                    "bird",
                    "cat",
                    "cow",
                    "lion",
                    "sheep",
                    "wolf",
                    "horse",
                    "monkey",
                    "owl",
                    "pig"
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setEventListeners();
        restartGame(); // starts the game
    }

    private final void setEventListeners() {

        ImageView[] imageViewArray = getImageViews();
        ImageButton imgButton = (ImageButton) findViewById(R.id.imgBtn);

        for (ImageView imageView: imageViewArray) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isCorrectAnimal((String) v.getTag()); // The image of a view is identified by the tag attribute
                }
            });
        }

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(currentAnimal); // currentAnimal as in the field/attribute
            }
        });
    }

    private final ImageView[] getImageViews() {
        int imageViewID;
        ImageView imageView;
        ImageView[] imageViewArray = new ImageView[NUMBER_OF_IMAGEVIEWS];

        for (int i = 0; i < NUMBER_OF_IMAGEVIEWS; i++) {
            imageViewID = getResources().getIdentifier("img" + i, "id", getPackageName());
            imageView = (ImageView) findViewById(imageViewID);
            imageViewArray[i] = imageView;
        }

        return imageViewArray;
    }

    private final void isCorrectAnimal(String clickedAnimal) {
        // Fades out all ImageViews and restarts the game if clickedAnimal == this.currentAnimal

        if (clickedAnimal == this.currentAnimal) {
            fadeOutImages(FADEOUTDURATION, clickedAnimal);

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            restartGame();
                        }
                    }, FADEOUTDURATION);
        }
    }

    private final void restartGame() {
        ArrayList<String> animalArray = createAnimalArray();
        updateCurrentAnimal(animalArray);
        updatePreviousAnimals(animalArray);
        insertImages(animalArray);
    }

    private final ArrayList<String> createAnimalArray() {
        Random randomObj = new Random();
        ArrayList<String> animalArray = new ArrayList<String>();

        String animal;
        int animalID;
        int numberOfAnimals = ANIMALS.length;

        while (animalArray.size() < NUMBER_OF_IMAGEVIEWS) {
            animalID = randomObj.nextInt(numberOfAnimals);
            animal = ANIMALS[animalID];
            if ( !(this.previousAnimals.contains(animal) || animalArray.contains(animal)) ) {
                animalArray.add(animal);
            }
        }

        return animalArray;
    }

    private final void updateCurrentAnimal(ArrayList<String> animalArray) {
        this.currentAnimal = animalArray.get(new Random().nextInt(NUMBER_OF_IMAGEVIEWS));
    }

    private final void insertImages(ArrayList<String> animalArray) {
        ImageView[] imageViewArray = getImageViews();

        Bitmap bm;
        ImageView imageView;

        int imageID;
        int imageViewIndex = 0;

        for (String animal: animalArray) {
            imageView = imageViewArray[imageViewIndex++];
            imageView.setTag(animal);

            imageID = getResources().getIdentifier(animal, "drawable", getPackageName());

            bm = decodeSampledBitmapFromResource(
                    getResources(), imageID, this.IMAGEWIDTH, this.IMAGEHEIGHT);

            imageView.setImageBitmap(
                    Bitmap.createScaledBitmap(bm, this.IMAGEWIDTH, this.IMAGEHEIGHT, false));
        }
    }

    private final void fadeOutImages(int duration, String except) {
        // fades out all ImageViews except the one with the tag "except"

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(duration);

        ImageView[] imageViewArray = getImageViews();

        for (ImageView imageView: imageViewArray) {
            if (imageView.getTag() != except) {
                imageView.startAnimation(fadeOut);
            }
        }
    }

    private final void playSound(String filename) {
        int soundFileID = getResources().getIdentifier(filename, "raw", getPackageName());

        if ( (mediaPlayer != null) && (mediaPlayer.isPlaying()) ) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(), soundFileID);
        mediaPlayer.start();
    }

    private final void updatePreviousAnimals(ArrayList<String> animalArray) {
        this.previousAnimals.clear();
        for (String animal: animalArray) {
            this.previousAnimals.add(animal);
        }
    }

    private final static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private final static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}