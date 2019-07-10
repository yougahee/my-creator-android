package scom.crecrew.crecre.UI.Fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import com.crecrew.crecre.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*
import com.crecrew.crecre.Base.BasePagerAdapter
import com.crecrew.crecre.Data.CreatorData
import com.crecrew.crecre.Data.LastVoteData
import com.crecrew.crecre.Data.TodayPost
import com.crecrew.crecre.Data.TodayRankData
import com.crecrew.crecre.Network.ApplicationController
import com.crecrew.crecre.Network.CreatorNetworkService
import com.crecrew.crecre.Network.Get.GetCreatorTodayHotRank
import com.crecrew.crecre.UI.Activity.Community.CommunityHotPostActivity
import com.crecrew.crecre.UI.Activity.VoteSuggestActivity
import com.crecrew.crecre.UI.Adapter.TodayPostRecyclerViewAdapter
import org.jetbrains.anko.support.v4.startActivity
import com.crecrew.crecre.UI.Activity.CreatorSearchActivity
import com.crecrew.crecre.UI.Adapter.TodayRankRecyclerViewAdapter
import com.crecrew.crecre.UI.Fragment.Home.ClosedVoteFragment
import com.crecrew.crecre.UI.Fragment.HomeTodayRankFragment
import com.crecrew.crecre.UI.View.SimpleDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_home_today_rank.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList


class HomeFragment: Fragment() {

    private lateinit var rootView: View
    private var isChartOpen = false

    lateinit var todayPostRecyclerViewAdapter: TodayPostRecyclerViewAdapter
    var todayCreatorRankData : ArrayList<CreatorData> = ArrayList()

    val creatorNetworkService: CreatorNetworkService by lazy{
        ApplicationController.instance.creatorNetworkService
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // 통신
        getCreatorTodayHotRank()




        // 화면 전환
        rootView.run {

            fragment_home_iv_today_hot_btn.setOnClickListener() {
                openTodayHotChart()
            }

            fragment_home_vote_recommendation_btn.setOnClickListener {
                startActivity<VoteSuggestActivity>()
            }
            fragment_home_txt_today_hot_post_more.setOnClickListener {
                startActivity<CommunityHotPostActivity>()
                // TODO: hot과 new 구분하기!
            }
            fragment_home_txt_today_new_post_more.setOnClickListener {
                startActivity<CommunityHotPostActivity>()
            }

            // search bar 누르면 검색 화면으로 넘어가기
            fragment_home_txt_search.setOnClickListener {
                val intent = Intent(activity, CreatorSearchActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)

            }

            // 다른 화면 누르면 키보드 내리고 edittext focus 삭제
            fragment_home_container.setOnClickListener {
                downKeyboard(fragment_home_container)
            }
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureRecyclerView()

    }

    override fun onResume() {
        super.onResume()

        /*
        fragment_home_edit_search.setText(null)
        fragment_home_edit_search.clearFocus();
*/
    }

    // 실시간 크리에이터 차트 열고 닫기
    private fun openTodayHotChart(){
        if(isChartOpen == false) {
            fragment_home_today_rank_container.visibility = VISIBLE
            fragment_home_iv_today_hot_btn.setImageResource(R.drawable.icn_hide_off)
            fragment_home_today_hot_creator_container.visibility = GONE
            isChartOpen = true
        }
        else {
            fragment_home_today_rank_container.visibility = GONE
            fragment_home_iv_today_hot_btn.setImageResource(R.drawable.icn_hide_on)
            fragment_home_today_hot_creator_container.visibility = VISIBLE
            isChartOpen = false
        }
    }

    private fun configureRecyclerView(){

        // last vote
        var lastVoteData: ArrayList<LastVoteData> = ArrayList()

        lastVoteData.add(LastVoteData("https://img.sbs.co.kr/newimg/news/20170907/201091232_1280.jpg","https://news.imaeil.com/inc/photos/2019/04/29/2019042900310562698_l.jpg", "시연조교",1,"크리크리 짱, 2줄 넘어갔을 때 처리도 해야 함 !! 홍루이젠 맛있다."))
        lastVoteData.add(LastVoteData("https://s-i.huffpost.com/gen/1771947/images/n-DEFAULT-628x314.jpg","https://mblogthumb-phinf.pstatic.net/MjAxODA1MTlfOSAg/MDAxNTI2NzQwNjY5OTUx.VcucGKX52noaAETS5acZgeovzLRSCWs8AkzGJVJUuasg.PIDUYkcbI_IaBRJ25-Lgu4-pnrDdVuP8uWK4ZRQbxl8g.JPEG.okyunju0309/PicsArt_05-19-01.19.40.jpg?type=w800", "가희바희보",1,"하나빼기 일 >___<"))
        lastVoteData.add(LastVoteData("https://www.sanghafarm.co.kr/sanghafarm_Data/upload/shop/product/201803/A0000101_2018032109513585717.jpg","", "현희여신",1,"오늘은 잼을 가져오셨다."))

        frag_home_vp_clsd.run {
            adapter = BasePagerAdapter(fragmentManager!!).apply {
                for (i in lastVoteData.indices)
                    addFragment(ClosedVoteFragment.newInstance(lastVoteData[i]))
            }
        }

        // hot post
        var todayHotDataList: ArrayList<TodayPost> = ArrayList()

        todayHotDataList.add(TodayPost("http://mblogthumb4.phinf.naver.net/MjAxODA1MTJfMTMx/MDAxNTI2MTMxOTAwMDQw.nYJ52P9m33uVBVaA4D9Y8aEpngi2BhTwZhlmPi11xnwg.jl2N0ZuJDxZDdTZZsNbrQTpGemFNgLS342YMnUcYeKMg.JPEG.jini2877/12.jpg?type=w800","딕헌터와 영알남 커플, 현실에서의 만남 더 심쿵! 하라락후루룩 호로록 줄이넘어간다링 안넘어간다", "먹방",89,32,"2019-07-07 15:12"))
        todayHotDataList.add(TodayPost("http://www.mrtt.news/news/photo/201810/1093_4186_485.jpg","2019년 7월 3일 현재 나는 배가 고프다. 어깨도 아픔", "개발",1004,52,"2019-07-07 04:51"))
        todayHotDataList.add(TodayPost("https://byline.network/wp-content/uploads/2018/12/tiye.png","지금 정호,예원,다연,민정,가희,신우,혁표,현희랑 같이 있음 크리크리짱", "솝트",999,14,"2019-07-07 23:12"))

        todayPostRecyclerViewAdapter = TodayPostRecyclerViewAdapter(activity!!, todayHotDataList)
        fragment_home_rv_today_hot_post.adapter = todayPostRecyclerViewAdapter
        fragment_home_rv_today_hot_post.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        fragment_home_rv_today_hot_post.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))

        // new post
        var todayNewDataList: ArrayList<TodayPost> = ArrayList()

        todayNewDataList.add(TodayPost("https://i.ytimg.com/vi/SzJo9QfhZg8/maxresdefault.jpg","왼쪽 어깨 너무 아프당. 내일 알바가기 시르다", "건강",89,32,"2019-07-07 23:50"))
        todayNewDataList.add(TodayPost("https://scontent-bru2-1.cdninstagram.com/vp/8689f4c9fc508fd674d2f60a5c23b2a0/5DC25F71/t51.2885-15/e35/56184620_383987605521824_7189641354343536955_n.jpg?_nc_ht=scontent-bru2-1.cdninstagram.com","와 좀있으면 홈화면 끝난다", "개발",1004,52,"2019-07-07 23:40"))
        todayNewDataList.add(TodayPost("http://static.hubzum.zumst.com/hubzum/2017/10/19/10/09828761654d4f02af8ba6cf86bc4cc3.png","헐 1일 1커밋해야하는데, 이제 커밋해야지", "개발",999,14,"2019-07-07 23:32"))

        todayPostRecyclerViewAdapter = TodayPostRecyclerViewAdapter(activity!!, todayNewDataList)
        fragment_home_rv_today_new_post.adapter = todayPostRecyclerViewAdapter
        fragment_home_rv_today_new_post.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        fragment_home_rv_today_new_post.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))

    }

    private fun downKeyboard(view: View) {

        val imm: InputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        // fragment_home_edit_search.clearFocus()
    }



    // creator ranking networking
    private fun getCreatorTodayHotRank(){

        val getCreatorTodayHotRank = creatorNetworkService.getCreatorTodayHotRank()
        getCreatorTodayHotRank.enqueue(object: Callback<GetCreatorTodayHotRank> {
            override fun onFailure(call: Call<GetCreatorTodayHotRank>, t: Throwable) {
                Log.e("creator search fail",t.toString())
            }

            override fun onResponse(call: Call<GetCreatorTodayHotRank>, response: Response<GetCreatorTodayHotRank>) {
                if(response.isSuccessful){
                    if(response.body()!!.status == 200){
                        todayCreatorRankData = response.body()!!.data!!

                        var todayCreatorRankTopData : ArrayList<CreatorData> = ArrayList(5)
                        var todayCreatorRankBottomData: ArrayList<CreatorData> = ArrayList(5)

                        var index = 0
                        for(i in 0 ..4)
                            todayCreatorRankTopData.add(todayCreatorRankData[index++])
                        for(i in 0 ..2)
                            todayCreatorRankBottomData.add(todayCreatorRankData[index++])

                        rootView.fragment_home_vp_today_rank.run {
                            adapter = BasePagerAdapter(fragmentManager!!).apply {

                                var homeTodayRankTopFragment: HomeTodayRankFragment = HomeTodayRankFragment()
                                homeTodayRankTopFragment.setList(todayCreatorRankTopData)

                                var homeTodayRankBottomFragment: HomeTodayRankFragment = HomeTodayRankFragment()
                                homeTodayRankBottomFragment.setList(todayCreatorRankBottomData)

                                addFragment(homeTodayRankTopFragment)
                                addFragment(homeTodayRankBottomFragment)
                            }
                        }

                        rootView.let {

                            // tab설정
                            it.fragment_home_tl_today_rank.run {
                                val navigationLayout: View =
                                    LayoutInflater.from(activity!!).inflate(R.layout.fragment_home_today_rank_navi, null, false)
                                setupWithViewPager(it.fragment_home_vp_today_rank)
                                getTabAt(0)!!.customView =
                                    navigationLayout.findViewById(R.id.fragment_home_today_rank_navi_top) as RelativeLayout
                                getTabAt(1)!!.customView =
                                    navigationLayout.findViewById(R.id.fragment_home_today_rank_navi_bottom) as RelativeLayout
                            }
                        }


                        /*
                        val topList: ArrayList<CreatorData> = ArrayList()
                        val bottomList: ArrayList<CreatorData> = ArrayList()

                        // tmp를 반으로 쪼개기
                        for(i in tmp.indices){
                            if(tmp[i].ranking <6){
                                topList[i] = tmp[i]
                            }
                            else{
                                bottomList[i-4] = tmp[i]
                            }
                        }
*/


                    }
                }
            }
        })
    }

}
