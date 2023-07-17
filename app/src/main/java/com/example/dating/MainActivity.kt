package com.example.dating

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import com.example.dating.databinding.ActivityMainBinding
import com.example.dating.slider.CardStackAdapter
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager : CardStackLayoutManager

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        manager = CardStackLayoutManager(baseContext, object: CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {
            }

            override fun onCardSwiped(direction: Direction?) {
            }

            override fun onCardRewound() {
            }

            override fun onCardCanceled() {
            }

            override fun onCardAppeared(view: View?, position: Int) {
            }

            override fun onCardDisappeared(view: View?, position: Int) {
            }

        })

        val testList = mutableListOf<String>()
        testList.add("a")
        testList.add("b")
        testList.add("c")

        binding.cardStackView.layoutManager = manager
        cardStackAdapter = CardStackAdapter(baseContext, testList)
        binding.cardStackView.adapter = cardStackAdapter

        setContentView(binding.root)

    }
}