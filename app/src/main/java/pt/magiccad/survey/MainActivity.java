package pt.magiccad.survey;
import android.Manifest;
import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.webkit.*;
import androidx.webkit.WebViewAssetLoader;
import android.widget.*;
import android.provider.MediaStore;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Base64;
import java.io.OutputStream;
public class MainActivity extends Activity {
 WebView web; GeolocationPermissions.Callback geoCallback; String geoOrigin;
 @Override public void onCreate(Bundle b){super.onCreate(b);web=new WebView(this);setContentView(web);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setAllowFileAccess(true);web.getSettings().setGeolocationEnabled(true);web.addJavascriptInterface(new Bridge(),"AndroidBridge");final WebViewAssetLoader loader = new WebViewAssetLoader.Builder().addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this)).build();web.setWebViewClient(new WebViewClient(){@Override public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request){return loader.shouldInterceptRequest(request.getUrl());}});web.setWebChromeClient(new WebChromeClient(){@Override public void onGeolocationPermissionsShowPrompt(String origin,GeolocationPermissions.Callback callback){geoOrigin=origin;geoCallback=callback;if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){callback.invoke(origin,true,false);}else requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},7);}});web.loadUrl("https://appassets.androidplatform.net/assets/index.html");}
 @Override public void onRequestPermissionsResult(int r,String[] p,int[] g){super.onRequestPermissionsResult(r,p,g);if(r==7&&geoCallback!=null){geoCallback.invoke(geoOrigin,g.length>0&&g[0]==PackageManager.PERMISSION_GRANTED,false);geoCallback=null;}}
 class Bridge {@JavascriptInterface public void saveFile(String filename,String base64,String mime){try{byte[] bytes=Base64.decode(base64,Base64.DEFAULT);ContentValues v=new ContentValues();v.put(MediaStore.Downloads.DISPLAY_NAME,filename.replace('/','_').replace('\\','_'));v.put(MediaStore.Downloads.MIME_TYPE,mime);v.put(MediaStore.Downloads.RELATIVE_PATH,"Download/MagicCADSurvey");Uri u=getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,v);if(u==null)throw new Exception("Falha ao criar ficheiro");try(OutputStream out=getContentResolver().openOutputStream(u)){out.write(bytes);}runOnUiThread(()->Toast.makeText(MainActivity.this,"Guardado em Downloads/MagicCADSurvey",Toast.LENGTH_LONG).show());}catch(Exception e){runOnUiThread(()->Toast.makeText(MainActivity.this,"Erro: "+e.getMessage(),Toast.LENGTH_LONG).show());}}}
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}