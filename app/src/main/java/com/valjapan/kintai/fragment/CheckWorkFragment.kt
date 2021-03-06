package com.valjapan.kintai.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.valjapan.kintai.R
import com.valjapan.kintai.adapter.RealmViewAdapter
import com.valjapan.kintai.adapter.WorkData
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_kakunin.view.*
import java.text.SimpleDateFormat
import java.util.*

class CheckWorkFragment : Fragment() {
    private var realm: Realm? = null
    private lateinit var recyclerView: RecyclerView
    private var date = Date()
    private var formatYear = SimpleDateFormat("yyyy", Locale.JAPANESE)
    private var formatMonth = SimpleDateFormat("MM", Locale.JAPANESE)
    private var year: Int = 0
    private var month: Int = 0
    private var realmResults: RealmResults<WorkData>? = null
    private lateinit var v: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        year = Integer.parseInt(formatYear.format(date))
        month = Integer.parseInt(formatMonth.format(date))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_kakunin, container, false)
        realm = Realm.getDefaultInstance()
        v.yearMonthTextView.text = "$year 年 $month 月"
        recyclerView = v.findViewById(R.id.check_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(v.context)
        searchRealm(v)
        v.beforeMonth.setOnClickListener {
            month--
            when (month) {
                0 -> {
                    year--
                    month = 12
                }
                13 -> {
                    year++
                    month = 1
                }
            }
            setText(v)
            searchRealm(v)
        }

        v.nextMonth.setOnClickListener {
            month++
            when (month) {
                0 -> {
                    year--
                    month = 12
                }
                13 -> {
                    year++
                    month = 1
                }
            }
            setText(v)
            searchRealm(v)
        }
        return v
    }

    private fun searchRealm(view: View) {
        realmResults = realm?.where(WorkData::class.java)
            ?.equalTo("year", year)
            ?.equalTo("month", month)
            ?.findAll()?.sort("startTime")
        recyclerView.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.adapter =
            RealmViewAdapter(view.context, realmResults, true)
    }

    private fun setText(view: View) {
        view.yearMonthTextView.text = "$year 年 $month 月"
    }
}
