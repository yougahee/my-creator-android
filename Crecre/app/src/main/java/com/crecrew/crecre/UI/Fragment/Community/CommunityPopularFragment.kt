package com.crecrew.crecre.UI.Fragment.Community

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crecrew.crecre.Data.CommunityHotPostData

import com.crecrew.crecre.R
import com.crecrew.crecre.UI.Adapter.CommunityHotPostRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_community_popular.*

class CommunityPopularFragment : Fragment() {

    lateinit var communityPopularRecyclerViewAdapter: CommunityHotPostRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community_popular, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()
    }

    //인기글 RecyclerView
    private fun setRecyclerView() {

        var dataList: ArrayList<CommunityHotPostData> = ArrayList()
        dataList.add(CommunityHotPostData("http://sopt.org/wp/wp-content/uploads/2014/01/24_SOPT-LOGO_COLOR-1.png",1,
            "햇님이 먹었던 과자 브랜드 이게 맞나?", 10, 10, "18:47","인기게시판",0,0))
        dataList.add(CommunityHotPostData("", 1,"안녀안알ㄴ여ㅏㄴ여낭",
            1, 8, "14:27","입짧은 햇님",0,0))
        dataList.add(CommunityHotPostData("",1,
            "입짧은햇님보세여", 19, 19, "12:35","온도",0,0))
        dataList.add(CommunityHotPostData("http://sopt.org/wp/wp-content/uploads/2014/01/24_SOPT-LOGO_COLOR-1.png",0,
            "먹방요정", 4, 8, "11:20","인기게시판",0,0))
        dataList.add(CommunityHotPostData("http://sopt.org/wp/wp-content/uploads/2014/01/24_SOPT-LOGO_COLOR-1.png",0,
            "바부야", 2, 7, "04:30","감성",0,0))


        communityPopularRecyclerViewAdapter = CommunityHotPostRecyclerViewAdapter(context!!, dataList)
        rv_popular_community_fg.adapter = communityPopularRecyclerViewAdapter
        rv_popular_community_fg.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

    }

}