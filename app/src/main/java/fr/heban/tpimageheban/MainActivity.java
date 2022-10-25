package fr.heban.tpimageheban;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    //On stocke l'uri original de l'image chargée
    private Uri originalUri = null;

    ActivityResultLauncher<String> res = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    //Recherche du textview responsable de l'affichage du chemin de l'image
                    TextView imagePath = (TextView) findViewById(R.id.imagePath);
                    //on met le chemin de l'image
                    imagePath.setText(result.toString());
                    ChargerImage(result);
                    originalUri = result;
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //on enregistre le composant image comme ayant un menu contextuel
        registerForContextMenu(findViewById(R.id.main_image));
    }

    public void onLoadImageClicked(View view) {
        //On lance une recherche mime avec tous les types d'images
        res.launch("image/*");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //on applique le menu contextuel uniquement si l'id correspond à l'image
        if (v.getId() == R.id.main_image) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.image_context_menu, menu);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        //On récupère l'image
        ImageView imageView = (ImageView) findViewById(R.id.main_image);
        //On vérifie que c'est pas null pour éviter des erreurs
        if (((BitmapDrawable) imageView.getDrawable()) == null) {
            return true;
        }
        //On le transforme en bitmap
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (bitmap == null) {
            return true;
        }
        //On définit la largeur et longueur maximale
        int maxHeight = bitmap.getHeight() - 1;
        int maxWidth = bitmap.getWidth() - 1;
        switch (id) {
            case R.id.horizontal_mirror_btn:

                /*Inversement des pixels sur la demi partie de la hauteur*/
                for (int y = 0; y < (maxHeight + 1) / 2; y++) {
                    for (int x = 0; x < maxWidth + 1; x++) {

                        int color = bitmap.getPixel(x, y);
                        int tmpColor = bitmap.getPixel(x, maxHeight - y);

                        bitmap.setPixel(x, y, tmpColor);
                        bitmap.setPixel(x, maxHeight - y, color);

                    }
                }


                break;
            case R.id.vertical_mirror_btn:
                /*Inversement des pixels sur la demi partie de la longueur*/
                for (int y = 0; y < (maxHeight + 1); y++) {
                    for (int x = 0; x < (maxWidth + 1) / 2; x++) {

                        int color = bitmap.getPixel(x, y);
                        int tmpColor = bitmap.getPixel(maxWidth - x, y);

                        bitmap.setPixel(x, y, tmpColor);
                        bitmap.setPixel(maxWidth - x, y, color);

                    }
                }
                break;
            case R.id.clockwise_direction_btn: //Sens horaire
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap.getHeight(),bitmap.getWidth(), Bitmap.Config.ARGB_8888);
                    for (int x = 0; x < (maxWidth + 1); x++) {
                        for (int y = 0; y < (maxHeight + 1); y++) {

                        bitmap1.setPixel(maxHeight-y,x, bitmap.getPixel(x,y));

                    }
                }
                imageView.setImageBitmap(bitmap1);

                break;
            case R.id.anti_clockwise_direction_btn: //Sens horaire
                Bitmap bitmap2 = Bitmap.createBitmap(bitmap.getHeight(),bitmap.getWidth(), Bitmap.Config.ARGB_8888);
                for (int x = 0; x < (maxWidth + 1); x++) {
                    for (int y = 0; y < (maxHeight + 1); y++) {

                        bitmap2.setPixel(y,maxWidth-x, bitmap.getPixel(x,y));

                    }
                }
                imageView.setImageBitmap(bitmap2);

                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        //On récupère l'image
        ImageView imageView = (ImageView) findViewById(R.id.main_image);
        //On vérifie que c'est pas null pour éviter des erreurs
        if (((BitmapDrawable) imageView.getDrawable()) == null) {
            return true;
        }
        //On le transforme en bitmap
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        if (bitmap == null) {
            return true;
        }
        //On définit la largeur et longueur maximale
        int maxHeight = bitmap.getHeight() - 1;
        int maxWidth = bitmap.getWidth() - 1;
        switch (item.getItemId()) {
            case R.id.inverse_color_btn: //Inversement des couleurs

                for (int y = 0; y < maxHeight; y++) {
                    for (int x = 0; x < maxWidth; x++) {
                        int pixel = bitmap.getPixel(x, y);
                        int newColor = Color.argb(Color.alpha(pixel), 255 - Color.red(pixel), 255 - Color.green(pixel), 255 - Color.blue(pixel));
                        bitmap.setPixel(x, y, newColor);
                    }
                }

                break;
            case R.id.tranform_in_grey_btn: // transformation en niveau de gris

                for (int y = 0; y < maxHeight; y++) {
                    for (int x = 0; x < maxWidth; x++) {

                        int pixel = bitmap.getPixel(x, y);
                        int mean = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;

                        int newColor = Color.argb(Color.alpha(pixel), mean, mean, mean);

                        bitmap.setPixel(x, y, newColor);

                    }
                }

                break;
            default:
                return true;
        }

        return true;
    }

    private void ChargerImage(Uri imageUri) {
        // ----- préparer les options de chargement de l’image
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inMutable = true; // l’image pourra être modifié
        // ------ chargement de l’image - valeur retournée null en cas d’erreur
        try {
            Bitmap bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri), null, option);
            ImageView image = (ImageView) findViewById(R.id.main_image);
            image.setImageBitmap(bm);

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found");
        }
    }

    public void onCancelActionBtnClicked(View view) {
        //On recharge l'image originelle
        ChargerImage(this.originalUri);

    }
}