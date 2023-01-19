-dontwarn com.petterp.floatingx.**
-keep public class com.petterp.floatingx.view.FxManagerView{*;}
-keep public class com.petterp.floatingx.view.FxViewHolder{*;}
-keep class * implements com.petterp.floatingx.listener.control.IFxAppControl { *; }
-keep class * implements com.petterp.floatingx.listener.control.IFxControl { *; }
-keep public class com.petterp.floatingx.util.ScreenExtKt{
  private boolean checkNavigationBarShow(android.content.Context);
}