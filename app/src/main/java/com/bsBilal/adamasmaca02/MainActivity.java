package com.bsBilal.adamasmaca02;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };


    List<String> triedWords = new ArrayList<>();

    String[] validWords=new String[250];

    //degiskenleri tanimlama
    TextView txtWordToBeGuessed;
    String wordToBeGuessed;
    ImageView imgHang;
    String wordDisplayedString;
    char[] wordDisplayedCharArray;
    ArrayList<String> myListOfWords;
    EditText edtInput;
    TextView txtLettersTried;
    TextView txtWonCount;
    String lettersTried;
    final String MESSAGE_WITH_LETTERS_TRIED = "Başarısız Denemeler : ";
    TextView txtTriesLeft;
    final String WINNING_MESSAGE = "KAZANDINIZ..!";
    final String LOSING_MESSAGE = "KAYBETTINIZ..!";
    final String WON_MESSAGE="Oturumdaki bildiginiz kelime sayısı: ";
    int wonCount=0;
    LottieAnimationView animationResult;

    public int Haklar=5;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();


        imgHang = (ImageView) findViewById(R.id.imgViewHang);

        //Degiskenlerin tanımlanmasi
        myListOfWords = new ArrayList<>();
        txtWordToBeGuessed = (TextView) findViewById(R.id.txtWordToBeGuessed);
        edtInput = (EditText) findViewById(R.id.edtInput);
        txtLettersTried = (TextView) findViewById(R.id.txtLettersTried);
        txtTriesLeft = (TextView) findViewById(R.id.txtTriesLeft);
        txtWonCount= (TextView) findViewById(R.id.txtWinCount);
        //animasyon tanımlaması
        animationResult = (LottieAnimationView) findViewById(R.id.WinAnimation);
        animationResult.setVisibility(View.INVISIBLE);
        animationResult.cancelAnimation();
        animationResult.setSpeed(0.6f);
        txtLettersTried.setVisibility(View.INVISIBLE);

        try{
            //if possible get datas from web
            new GetWordsFromWebSite().execute();

        }
        catch(Exception ex){
            //if not then take from file
            GetWordsFromFile();
        }
    }//onCreate


    //harfin olup olmadıgını kontrol et
    private void checkIfLetterIsWord(char letter) {
        //harf bulundugunda calısıcak fonksiyon
        if (wordToBeGuessed.indexOf(letter) >= 0) {


            //eger harf gosterirlmemişse
            if (wordDisplayedString.indexOf(letter) < 0) {
                RevealLetterInTheWord(letter);
                //ekrandakileri guncelle
                DisplayWordOnScreen();
                //tum harfler tahmin edilmiş mi
                if (!wordDisplayedString.contains("_"))
                    MethodWIN();


            }
        }

        else {//harf kelimede yoksa
            txtLettersTried.setVisibility(View.VISIBLE);

            //eger harf listede yoksa ekle

            if (!triedWords.contains(String.valueOf(letter))) {
                triedWords.add(String.valueOf(letter));

                Haklar--;
                decreaseAndDisplayTriesLeft();
                imgHang.setVisibility(View.VISIBLE);

                if (Haklar==0) {
                    MethodLost();
                }
                else if(Haklar==1)
                    imgHang.setImageResource(R.drawable.man2);
                else if(Haklar==2)
                    imgHang.setImageResource(R.drawable.man3);
                else if(Haklar==3)
                    imgHang.setImageResource(R.drawable.man4);
                else if(Haklar==4)
                    imgHang.setImageResource(R.drawable.man5);

            }
            //denenen harfleri gosterme
            if(lettersTried.indexOf(letter)<0){
                lettersTried += letter + ", ";
                String messageToBeDisplayed = MESSAGE_WITH_LETTERS_TRIED + lettersTried;
                txtLettersTried.setText(messageToBeDisplayed);
            }
        }
    }

    private void MethodWIN() {

        wordDisplayedCharArray = wordToBeGuessed.toCharArray();

        String formattedString ="";
        for (char chr : wordDisplayedCharArray) {
            formattedString+=chr+" ";
        }
        txtWordToBeGuessed.setText(formattedString);
        animationResult.setVisibility(View.VISIBLE);
        animationResult.setAnimation("WinConfetti.json");
        animationResult.playAnimation();
        txtTriesLeft.setText(WINNING_MESSAGE);
        wonCount++;
        txtWonCount.setText(WON_MESSAGE+wonCount);
        edtInput.setEnabled(false);
    }

    //hak dsurme
    private void decreaseAndDisplayTriesLeft() {
        //eger hakkı varsa
        if (Haklar!=0)
            txtTriesLeft.setText("Kalan Hakkınız : " + Haklar);
    }

    //get words
    void GetWordsFromFile()
    {
        //Traverse database and get datas
        InputStream myInputStream = null;
        Scanner in = null;
        String aWord="";

        try {
            myInputStream=getAssets().open("database_file.txt");
            in=new Scanner(myInputStream);
            while(in.hasNext()){

                aWord=in.next();
                myListOfWords.add(aWord);
            }


        } catch (IOException e) {
            Toast.makeText(this,e.getClass().getSimpleName()+": "+e.getMessage(),
                    Toast.LENGTH_SHORT);

        }
        finally {
            if(in!=null)
                in.close();

            try {
                if(myInputStream!=null)
                    myInputStream.close();
            } catch (IOException e) {
                Toast.makeText(this,e.getClass().getSimpleName()+": "+e.getMessage(),
                        Toast.LENGTH_SHORT);
            }

        }//close Scanner and input stream

    }


    //yeni oyun olusturma
    private void initializeGame() {
        //aynı kelimeyi sormamak icin ilkini al ve karıştır
        Collections.shuffle(myListOfWords);
        wordToBeGuessed = myListOfWords.get(0);
        myListOfWords.remove(0);
        triedWords.clear();
        //kelime arrayini basma
        wordDisplayedCharArray = wordToBeGuessed.toCharArray();

        //kelimenin ekranda gozukmesi
        for (int i = 1; i < wordDisplayedCharArray.length - 1; i++)
            wordDisplayedCharArray[i] = '_';


        RevealLetterInTheWord(wordDisplayedCharArray[0]);

        RevealLetterInTheWord(wordDisplayedCharArray[wordDisplayedCharArray.length - 1]);

        wordDisplayedString = String.valueOf(wordDisplayedCharArray);
        DisplayWordOnScreen();


        lettersTried = " ";
        //EKRANDA DENENEN HARF YAZISINI GOSTERME
        txtLettersTried.setText(MESSAGE_WITH_LETTERS_TRIED);

        //KALAN HAKLAR
        Haklar = 5;
        txtTriesLeft.setText("Kalan Hakkınız : " + Haklar);

    }


    //harfleri ekranda gosterme
    private void DisplayWordOnScreen() {
        String formattedString ="";
        for (char chr : wordDisplayedCharArray) {
            formattedString+=chr+" ";
        }
        txtWordToBeGuessed.setText(formattedString);
    }

    private void RevealLetterInTheWord(char letter) {
        int indexOfLetter = this.wordToBeGuessed.indexOf(letter);
        while (indexOfLetter >= 0) {
            wordDisplayedCharArray[indexOfLetter] = wordToBeGuessed.charAt(indexOfLetter);
            indexOfLetter = this.wordToBeGuessed.indexOf(letter, indexOfLetter + 1);
        }
        wordDisplayedString = String.valueOf(wordDisplayedCharArray);
    }


    public void resetGame(View view) {
        txtLettersTried.setVisibility(View.INVISIBLE);

        //yeni oyun baslatma
        initializeGame();
        imgHang.setVisibility(View.INVISIBLE);

        edtInput.setEnabled(true);
        if (animationResult.isAnimating()) {
            animationResult.cancelAnimation();
            animationResult.setVisibility(View.INVISIBLE);

        } else
            animationResult.playAnimation();
    }

    public void btnTry(View view) {




        //buton boş mu degil mi
        if(edtInput.isEnabled())
        {
          //  Toast.makeText(this, wordToBeGuessed, Toast.LENGTH_SHORT).show();

            String Kelime = edtInput.getText().toString();
            if (edtInput.length() == 0)
                Toast.makeText(this, "Lütfen önce harf girin", Toast.LENGTH_SHORT).show();
            else if(edtInput.length() == 1)//Harf Denemesi
                checkIfLetterIsWord(Kelime.charAt(0));
            else//kelime denemesi
                CheckIfGuessedEqualtoWord(Kelime);

            edtInput.setText("");
        }

        else {
            Toast.makeText(this, "Lütfen önce yeni oyun başlatın..", Toast.LENGTH_SHORT).show();
        }
    }

    private void CheckIfGuessedEqualtoWord(String Kelime) {
        if (Kelime.compareTo(wordToBeGuessed)==0) {
            MethodWIN();

        } else {//tahmin edilen kelime dogru degilse
            txtLettersTried.setVisibility(View.VISIBLE);

            if(!triedWords.contains(Kelime)&& Haklar>1) {
                Haklar--;

                triedWords.add(Kelime);
                lettersTried += Kelime + ", ";
                String messageToBeDisplayed = MESSAGE_WITH_LETTERS_TRIED + lettersTried;
                txtLettersTried.setText(messageToBeDisplayed);
                decreaseAndDisplayTriesLeft();
            }
            else if(Haklar==1){
                lettersTried += Kelime + ", ";
                String messageToBeDisplayed = MESSAGE_WITH_LETTERS_TRIED + lettersTried;
                txtLettersTried.setText(messageToBeDisplayed);

                MethodLost();
            }
            else
                Toast.makeText(this, "Bu kelime daha önce denenmiş", Toast.LENGTH_SHORT).show();



        }
    }

    private void MethodLost() {

        imgHang.setImageResource(R.drawable.man1);
        txtTriesLeft.setText(LOSING_MESSAGE);
        animationResult.setAnimation("LostAnimate.json");
        animationResult.setVisibility(View.VISIBLE);
        animationResult.playAnimation();
        txtWordToBeGuessed.setText(wordToBeGuessed);
        edtInput.setEnabled(false);

    }


    private  class GetWordsFromWebSite extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle("Bilgilendirme");
            progressDialog.setMessage("Yeni kelimeler alınıyor...");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            GetWordsandParse();


            return null;
        }

        //get words on website
        private void GetWordsandParse() {


            try{
                int randomNum = ThreadLocalRandom.current().nextInt(120000, 800000);
                String newURL="https://www.memurlar.net/haber/"+randomNum;
                System.out.println(newURL);
                Document doc  = Jsoup.connect(newURL).get();
                Elements trs = doc.select("div.NewsDetail");
                for (Element tr : trs) {
                    String strDesc = tr.text();
                    String[] arrOfWords = strDesc.split(" ", 250);

                    int cursor = 0;
                    for (String a : arrOfWords) {

                        if (a.length() > 5
                                && a.length()<12
                                &&!containsIllegals(a))
                            validWords[cursor++] = a.toLowerCase();
                    }

                }


            }catch (Exception e){

                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            WriteToFile();
            ReadFromFile();

        }

        private void ReadFromFile() {
            String[] TakenWords=new String[250];
            int Cursor=0;
            progressDialog.setTitle("Bilgilendirme");
            progressDialog.setMessage("Yeni Kelimeler Yükleniyor...");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
            try{
                JsonReader jsonReader = new JsonReader(new InputStreamReader((openFileInput("data.json"))));
                try{
                    jsonReader.beginObject();

                    while (jsonReader.hasNext()){
                        String name = jsonReader.nextName();
                        if("Kelime".equals(name))
                            TakenWords[Cursor++]=jsonReader.nextString();
                        else{
                            //Unkhonw attribute
                            jsonReader.skipValue();
                        }
                    }
                    jsonReader.endObject();
                }finally {
                    jsonReader.close();
                }


                progressDialog.dismiss();


                for (String word : TakenWords) {
                    if (word != null)
                        myListOfWords.add(word);//addd words
                }
                initializeGame();
            }catch (IOException e){
                GetWordsFromFile();
                e.printStackTrace();
            }
        }

        private void WriteToFile()
        {

            try{
                JsonWriter jsonWriter = new JsonWriter(
                        new OutputStreamWriter(openFileOutput("data.json" , MODE_PRIVATE)));
                try{
                    jsonWriter.setIndent("  ");
                    jsonWriter.beginObject();

                    for (String word : validWords) {
                        if (word != null) {
                            jsonWriter.name("Kelime");
                            jsonWriter.value(word);
                        }
                        else
                            continue;
                    }
                    jsonWriter.endObject();
                }finally {
                    jsonWriter.close();
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }
            progressDialog.dismiss();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }
    //get permisions
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }


    public boolean containsIllegals(String toExamine) {
        Pattern pattern = Pattern.compile("[.,:;1'2'\3\'\'4567890~#@*+%{}<>\\[\\]|\"\\_^]");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }


}

