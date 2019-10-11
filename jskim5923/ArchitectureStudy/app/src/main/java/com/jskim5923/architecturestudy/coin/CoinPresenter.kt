package com.jskim5923.architecturestudy.coin

import com.jskim5923.architecturestudy.base.BasePresenter
import com.jskim5923.architecturestudy.extension.getCoinCurrency
import com.jskim5923.architecturestudy.model.data.source.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class CoinPresenter(private val view: CoinContract.View) : BasePresenter(), CoinContract.Presenter {
    override fun getTickerList(market: String?) {
        Repository.getMarketList()
            .subscribeOn(Schedulers.io())
            .flatMap { marketList ->
                Repository.getTicker(
                    marketList.asSequence()
                        .filter {
                            it.market.getCoinCurrency() == market
                        }.toList().joinToString(",") {
                            it.market
                        }
                )
            }
            .flattenAsObservable { tickerResponseList ->
                tickerResponseList.asSequence()
                    .map { tickerResponse ->
                        tickerResponse.toTicker()
                    }.toList()
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ tickerList ->
                view.updateRecyclerView(tickerList)
            }, { e ->
                e.printStackTrace()
            })
            .addTo(compositeDisposable)
    }
}