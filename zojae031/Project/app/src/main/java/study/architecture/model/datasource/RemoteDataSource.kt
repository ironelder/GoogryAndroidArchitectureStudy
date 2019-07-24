package study.architecture.model.datasource

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import study.architecture.model.vo.Market
import study.architecture.model.vo.Ticker

/**
 * Data 관리하는 비즈니스로직이 담긴 클래스
 */
object RemoteDataSource : UpbitApi {


    private const val url = "https://api.upbit.com/v1/"


    private val client =
        Retrofit
            .Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    private val api = client.create(UpbitApi::class.java)

    override fun getMarkets(): Single<List<Market>> =
        api.getMarkets()
            .subscribeOn(Schedulers.io())

    override fun getTickers(markets: String): Single<MutableList<Ticker>> =
        api.getTickers(markets)
            .subscribeOn(Schedulers.io())


}