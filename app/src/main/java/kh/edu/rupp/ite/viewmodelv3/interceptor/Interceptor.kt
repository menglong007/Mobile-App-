import kh.edu.rupp.ite.viewmodelv3.globle.App
import kh.edu.rupp.ite.viewmodelv3.globle.AppEncrypted
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = AppEncrypted.get().getToken(App.get())

        if (token == null){
            return chain.proceed(chain.request())
        } else {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            return chain.proceed(request)
        }
    }

}
