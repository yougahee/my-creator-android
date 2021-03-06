package com.crecrew.crecre.ui.activity.Community

import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import com.crecrew.crecre.ui.adapter.CommunityDetailCommentRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_community_detail.*
import com.bumptech.glide.Glide
import com.crecrew.crecre.db.SharedPreferenceController
import com.crecrew.crecre.data.CommunityDetailData
import com.crecrew.crecre.data.CommunityReplyData
import com.crecrew.crecre.network.ApplicationController
import com.crecrew.crecre.network.CommunityNetworkService
import com.crecrew.crecre.network.get.*
import com.crecrew.crecre.network.post.PostCommunityFavoriteLikeResponse
import com.crecrew.crecre.R
import com.crecrew.crecre.ui.activity.LoginActivity
import com.crecrew.crecre.databinding.ActivityCommunityDetailBinding
import com.crecrew.crecre.utils.*
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.jetbrains.anko.ctx
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CommunityDetailActivity : AppCompatActivity(), View.OnClickListener {

    fun onClickfinish(view : View) {
        finish()
    }

    var btn_like = 0
    var btn_unlike = 0
    var btn_anonymous = 0

    var like_cnt = 0
    var hate_cnt = 0

    var login_userIdx = -1
    var write_userIdx = -1

    var title: String? = ""
    var postidx = -1
    var category_title: String? = ""
    var postIdx: Int = -1



    lateinit var communityDetailCommentRecyclerViewAdapter: CommunityDetailCommentRecyclerViewAdapter

    val requestDialog: ContentsDeleteDialog by lazy {
        ContentsDeleteDialog(
            this@CommunityDetailActivity, "알림", "게시물을 정말 삭제하시겠어요?", "네"
            , "아니요", completeConfirmListener, completeNoListener
        )
    }

    val requestEnterDialog: SearchAlarmDialog by lazy {
        SearchAlarmDialog(
            this@CommunityDetailActivity, "알림", "댓글을 입력해주세요.",
            completefailConfirmListener, "확인"
        )
    }

    val writeSelfDialog: SearchAlarmDialog by lazy {
        SearchAlarmDialog(
            this@CommunityDetailActivity, "알림", "게시자가 아닙니다.",
            completefailConfirmListener, "확인"
        )
    }

    val readyDialog: SearchAlarmDialog by lazy {
        SearchAlarmDialog(
            this@CommunityDetailActivity, "알림", "준비중인 기능입니다.",
            completefailConfirmListener, "확인"
        )
    }

    val customLoginCheckDialog: CustomLoginCheckDialog by lazy {
        CustomLoginCheckDialog(
            this@CommunityDetailActivity, "알림", "로그인이 필요한 서비스입니다.", "로그인 하시겠습니까?", "네"
            , "아니요", completeLoginConfirmListener, completeNoListener
        )
    }

    val communityNetworkService: CommunityNetworkService by lazy {
        ApplicationController.instance.communityNetworkService
    }

    //click
    override fun onClick(v: View?) {
        when (v!!) {

            //추천 누르기
            btn_like_community_detail_act -> {

                if(ApplicationData.auth == "")
                    customLoginCheckDialog.show()
                else
                {
                    if (btn_unlike == 1) {
                        btn_unlike_community_detail_act.isSelected = false
                        tv_unlike_community_detail_act.setTextColor(Color.parseColor("#a4a4a4"))
                        tv_unlike_community_detail_act.setText("비추천 " + --hate_cnt)

                        //비추천 통신
                        postLikeOnClickResponse(
                            communityNetworkService.deletePostunhateOff(
                                ApplicationData.auth, postidx
                            ), 0
                        )
                        btn_unlike = 0

                        if (btn_like == 0) {
                            btn_like_community_detail_act.isSelected = true
                            tv_recommend_number_detail_act.setTextColor(Color.parseColor("#ff4343"))
                            tv_recommend_number_detail_act.setText("추천 " + ++like_cnt)

                            //좋아요 통신
                            postLikeOnClickResponse(
                                communityNetworkService.postPostLikeOn(
                                    ApplicationData.auth, postidx
                                ), 0
                            )
                            btn_like = 1
                        } else {
                            btn_like_community_detail_act.isSelected = false
                            tv_recommend_number_detail_act.setTextColor(Color.parseColor("#a4a4a4"))
                            tv_recommend_number_detail_act.setText("추천 " + --like_cnt)

                            //좋아요 취소 통신
                            postLikeOnClickResponse(
                                communityNetworkService.deletePostLikeOff(
                                    ApplicationData.auth, postidx
                                ), 0
                            )
                            btn_like = 0
                        }

                    } else {
                        if (btn_like == 0) {
                            btn_like_community_detail_act.isSelected = true
                            tv_recommend_number_detail_act.setTextColor(Color.parseColor("#ff4343"))
                            btn_like = 1
                            tv_recommend_number_detail_act.setText("추천 " + ++like_cnt)

                            //좋아요 통신
                            postLikeOnClickResponse(
                                communityNetworkService.postPostLikeOn(ApplicationData.auth, postidx),0)
                        } else {
                            btn_like_community_detail_act.isSelected = false
                            tv_recommend_number_detail_act.setTextColor(Color.parseColor("#a4a4a4"))
                            tv_recommend_number_detail_act.setText("추천 " + --like_cnt)
                            //좋아요 취소 통신
                            postLikeOnClickResponse(
                                communityNetworkService.deletePostLikeOff(
                                    ApplicationData.auth, postidx
                                ),0
                            )
                            btn_like = 0
                        }
                    }
                }
            }

            //비추천 누르기
            btn_unlike_community_detail_act -> {

                if (ApplicationData.auth == "") {
                    customLoginCheckDialog.show()
                } else {
                    if (btn_like == 1) {
                        btn_like_community_detail_act.isSelected = false
                        tv_recommend_number_detail_act.setTextColor(Color.parseColor("#a4a4a4"))
                        tv_recommend_number_detail_act.setText("추천 " + --like_cnt)
                        //좋아요 취소 통신
                        postLikeOnClickResponse(
                            communityNetworkService.deletePostLikeOff(
                                ApplicationData.auth, postidx
                            ),0
                        )
                        btn_like = 0

                        if (btn_unlike == 0) {
                            btn_unlike_community_detail_act.isSelected = true
                            tv_unlike_community_detail_act.setTextColor(Color.parseColor("#367fff"))
                            tv_unlike_community_detail_act.setText("비추천 " + ++hate_cnt)
                            //비추천 통신
                            postLikeOnClickResponse(
                                communityNetworkService.postPosthateOn(
                                    ApplicationData.auth, postidx
                                ),0
                            )
                            btn_unlike = 1
                        } else {
                            btn_unlike_community_detail_act.isSelected = false
                            tv_unlike_community_detail_act.setTextColor(Color.parseColor("#a4a4a4"))
                            tv_unlike_community_detail_act.setText("비추천 " + --hate_cnt)
                            //비추천 삭제 통신
                            postLikeOnClickResponse(
                                communityNetworkService.deletePostunhateOff(
                                    ApplicationData.auth, postidx
                                ),0
                            )
                            btn_unlike = 0
                        }
                    } else {
                        if (btn_unlike == 0) {
                            btn_unlike_community_detail_act.isSelected = true
                            tv_unlike_community_detail_act.setTextColor(Color.parseColor("#367fff"))
                            tv_unlike_community_detail_act.setText("비추천 " + ++hate_cnt)
                            //비추천 통신
                            postLikeOnClickResponse(
                                communityNetworkService.postPosthateOn(
                                    ApplicationData.auth
                                    , postidx
                                ),0
                            )
                            btn_unlike = 1
                        } else {
                            btn_unlike_community_detail_act.isSelected = false
                            tv_unlike_community_detail_act.setTextColor(Color.parseColor("#a4a4a4"))
                            tv_unlike_community_detail_act.setText("비추천 " + --hate_cnt)
                            //비추천 delete통신
                            postLikeOnClickResponse(
                                communityNetworkService.deletePostunhateOff(
                                    ApplicationData.auth
                                    , postidx
                                ),0
                            )
                            btn_unlike = 0
                        }
                    }
                }
            }

            //익명버튼
            btn_anonymous_detail_com_act -> {
                if (btn_anonymous == 0) {
                    btn_anonymous_detail_com_act.isSelected = true
                    btn_anonymous = 1
                } else {
                    btn_anonymous_detail_com_act.isSelected = false
                    btn_anonymous = 0
                }
            }

            //키보드 down
            rl_commu_detail_act -> {
                downKeyboard(rl_commu_detail_act)
            }

            //글 삭제버튼(본인이 작성한 경우에만 해당됨)
            btn_delete_detail_community_act -> {
                showPopup(btn_delete_detail_community_act)
            }

            //댓글 작성 버튼
            btn_find_search_com_act -> {

                if(ApplicationData.auth == "")
                    customLoginCheckDialog.show()
                else
                {
                    if (et_commnets_reply_commu_detail_act.text.length == 0) {
                        requestEnterDialog.show()
                    } else {
                        postCommentReplyResponse()

                    }
                }


            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_community_detail)
        val binding : ActivityCommunityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_community_detail)
        //val binding: ActivityCommunityDetailBinding = ActivityCommunityDetailBinding.inflate(getLayoutInflater())

//        binding.communityDetail

        ApplicationData.auth = SharedPreferenceController.getUserToken(this)
        Log.v("login_token", ApplicationData.auth)

        init()

        configureRecyclerView()

        configurebasicSetting()

        //detailAct 통신
        getCommunityRecentAllResponse()

        //댓글창
        getPostReplyResponse(1)

    }

    //팝업 클릭리스너
    val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn_delete_detail_community_act -> {
                showPopup(view)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val myMenuInflater = menuInflater
        myMenuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return true
    }

    private fun init() {
        btn_like_community_detail_act.setOnClickListener(this)
        btn_unlike_community_detail_act.setOnClickListener(this)
        btn_anonymous_detail_com_act.setOnClickListener(this)
        rl_commu_detail_act.setOnClickListener(this)
        btn_delete_detail_community_act.setOnClickListener(this)
        btn_find_search_com_act.setOnClickListener(this)
    }

    //댓글 recyclerView
    private fun configureRecyclerView() {
        var dataList: ArrayList<CommunityReplyData> = ArrayList()

        communityDetailCommentRecyclerViewAdapter = CommunityDetailCommentRecyclerViewAdapter(this, dataList)
        rv_comment_commu_detail_act.adapter = communityDetailCommentRecyclerViewAdapter
        rv_comment_commu_detail_act.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }


    private fun configurebasicSetting() {
        category_title = intent.getStringExtra("category_title")
        title = intent.getStringExtra("title")
        postidx = intent.getIntExtra("postidx", -1)
    }

    //키보드 다운
    private fun downKeyboard(view: View) {
        val imm: InputMethodManager =
            applicationContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //팝업창
    private fun showPopup(view: View) {
        var popup = PopupMenu(this, view)
        popup.inflate(R.menu.menu_main)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.menu_modified -> {
                    //준비중인 기능입니다.
                    readyDialog.show()
                }
                R.id.menu_delete -> {
                    //###여긴 다시!!!
                    if (login_userIdx == write_userIdx)
                    //삭제하시겠습니까?
                        requestDialog.show()
                    else if(ApplicationData.auth == "")
                        customLoginCheckDialog.show()
                    else
                    //본인이 아닙니다.
                        writeSelfDialog.show()
                }
            }
            true
        })

        popup.show()
    }

    //다이얼로그 -> 네
    private val completeConfirmListener = View.OnClickListener {
        postLikeOnClickResponse(communityNetworkService.deletePostComments(ApplicationData.auth, postidx),1)
    }

    //다이얼로그 -> 아니요
    private val completeNoListener = View.OnClickListener {
        requestDialog!!.dismiss()
        customLoginCheckDialog!!.dismiss()
    }

    //다이얼로그 -> 로그인하시겠습니까? -> 네
    private val completeLoginConfirmListener = View.OnClickListener {
        customLoginCheckDialog!!.dismiss()
        startActivity<LoginActivity>()
        //##쌓여있는 엑티비티 지우기...근데 마지막 상태로 돌아와야 할 것 같은데?
        finish()
    }

    //다이얼로그 -> 댓글
    private val completefailConfirmListener = View.OnClickListener {
        requestEnterDialog!!.dismiss()
        writeSelfDialog!!.dismiss()
        readyDialog!!.dismiss()
    }

    //통신 detailact 보여주기
    private fun getCommunityRecentAllResponse() {

        val getCommunityDetail: Call<GetPostDetailResponse> = communityNetworkService.getPostDetailPostIdx(
            ApplicationData.auth, postidx
        )

        getCommunityDetail.enqueue(object : Callback<GetPostDetailResponse> {
            override fun onFailure(call: Call<GetPostDetailResponse>, t: Throwable) {
                Log.e("Community detail fail", t.toString())
            }

            override fun onResponse(call: Call<GetPostDetailResponse>, response: Response<GetPostDetailResponse>) {
                Log.e("is Successful", response.isSuccessful.toString())
                if (response.isSuccessful) {
                    // response.message()
                    //if(response.status == 200) {
                    val temp: ArrayList<CommunityDetailData> = response.body()!!.data
                    if (temp.size >= 0)
                        makeView(temp)
                    //}
                }
            }
        })
    }

    private fun makeView(dataList: ArrayList<CommunityDetailData>) {
        //toolbar title
        tv_category_community_detail_act.text = dataList[0].board_name
        //contents title
        tv_title_community_detail_act.text = dataList[0].title

        postIdx = dataList[0].post_idx

        var boardIdx = dataList[0].board_idx
        write_userIdx = dataList[0].write_user_idx
        login_userIdx = dataList[0].login_userIdx


        btn_like = dataList[0].is_like
        btn_unlike = dataList[0].is_hate

        if (btn_like == 1) {
            btn_like_community_detail_act.isSelected = true
            tv_recommend_number_detail_act.setTextColor(Color.parseColor("#ff4343"))
        } else {
            btn_like_community_detail_act.isSelected = false
            tv_recommend_number_detail_act.setTextColor(Color.parseColor("#a4a4a4"))
        }

        if(btn_unlike == 1){
            btn_unlike_community_detail_act.isSelected = true
            tv_unlike_community_detail_act.setTextColor(Color.parseColor("#367fff"))
        } else{
            btn_unlike_community_detail_act.isSelected = false
            tv_unlike_community_detail_act.setTextColor(Color.parseColor("#a4a4a4"))
        }

        //user_porfile
        if (dataList[0].profile_url == null)
            Glide.with(ctx).load(R.drawable.icn_profile).into(img_user_profile_circleImageView)
        else
            Glide.with(ctx).load(dataList[0].profile_url).into(img_user_profile_circleImageView)

        //nickname
        if(dataList[0].is_anonymous == 1)
            tv_user_nickname_detail_act.setText("익명")
        else
            tv_user_nickname_detail_act.setText(dataList[0].nickname)

        //create_time
        tv_createtime_commu_detail_act.setText(dataList[0].create_time)

        //조회수
        tv_viewcnt_commu_detail_act.setText("조회수 " + dataList[0].view_cnt)

        //추천수
        like_cnt = dataList[0].like_cnt
        Log.v("추천수", like_cnt.toString())
        tv_recommend_number_detail_act.setText("추천 " + dataList[0].like_cnt)

        //비추천수
        hate_cnt = dataList[0].hate_cnt
        Log.v("비추천수", hate_cnt.toString())
        tv_unlike_community_detail_act.setText("비추천 " + dataList[0].hate_cnt)

        //사진
        if (dataList[0].media_url == "")
            img_main_community_detail.visibility = View.INVISIBLE
        else
            Glide.with(ctx).load(dataList[0].media_url).into(img_main_community_detail)

        //글 내용
        tv_main_context_community_detail.text = dataList[0].contents
    }

    //추천 누르기 보여주기 통신
    fun postLikeOnClickResponse(networkfunction: Call<PostCommunityFavoriteLikeResponse>, flag : Int) {
        val postPostLikeOn: Call<PostCommunityFavoriteLikeResponse> = networkfunction

        postPostLikeOn.enqueue(object : Callback<PostCommunityFavoriteLikeResponse> {

            override fun onFailure(call: Call<PostCommunityFavoriteLikeResponse>, t: Throwable) {
                Log.e("추천 누르기 fail", t.toString())
            }

            override fun onResponse(
                call: Call<PostCommunityFavoriteLikeResponse>, response: Response<PostCommunityFavoriteLikeResponse>
            ) {
                if (response.isSuccessful) {

                    if(flag == 1)
                    {
                        requestDialog!!.dismiss()
                        finish()
                    }

                }
            }
        })
    }

    //댓글 조회 get
    fun getPostReplyResponse(flag: Int) {
        val postPostLikeOn: Call<GetPostReplyResponse> = communityNetworkService.getPostReply(
            ApplicationData.auth, postidx
        )

        postPostLikeOn.enqueue(object : Callback<GetPostReplyResponse> {

            override fun onFailure(call: Call<GetPostReplyResponse>, t: Throwable) {
                Log.e("댓글 fail", t.toString())
            }

            override fun onResponse(
                call: Call<GetPostReplyResponse>, response: Response<GetPostReplyResponse>
            ) {
                var temp: ArrayList<CommunityReplyData> = response.body()!!.data

                //마지막 dataList만 넣어주기
                if (flag == 0) {
                    /*
                    for(i in 0..temp.size-1)
                        Log.v("click!",  temp[i].content)
                    */

                    communityDetailCommentRecyclerViewAdapter.dataList.add(temp[temp.size - 1])
                    communityDetailCommentRecyclerViewAdapter.notifyItemInserted(temp.size - 1)
                } else {
                    if (response.isSuccessful) {

                        val position = communityDetailCommentRecyclerViewAdapter.itemCount
                        communityDetailCommentRecyclerViewAdapter.dataList.addAll(temp)
                        communityDetailCommentRecyclerViewAdapter.notifyDataSetChanged()
                        //communityDetailCommentRecyclerViewAdapter.notifyItemInserted(position)

                    }
                }
            }
        })
    }

    //댓글 작성 post
    fun postCommentReplyResponse() {

        var content: String = et_commnets_reply_commu_detail_act.text.toString()
        //익명인지 아닌지 체크하

        //Json 형식의 객체 만들기
        var jsonObject = JSONObject()
        jsonObject.put("postIdx", postIdx)
        jsonObject.put("comments", content)
        jsonObject.put("is_anonymous", btn_anonymous)

        val gsonObject = JsonParser().parse(jsonObject.toString()) as JsonObject

        val postReplyResponse: Call<PostCommunityFavoriteLikeResponse> = communityNetworkService.postCommentReply(
            ApplicationData.auth
            , gsonObject
        )

        postReplyResponse.enqueue(object : Callback<PostCommunityFavoriteLikeResponse> {

            override fun onFailure(call: Call<PostCommunityFavoriteLikeResponse>, t: Throwable) {
                Log.e("댓글 fail", t.toString())
            }

            override fun onResponse(
                call: Call<PostCommunityFavoriteLikeResponse>,
                response: Response<PostCommunityFavoriteLikeResponse>
            ) {
                if (response.isSuccessful) {
                    //댓글창의 글자를 지우고
                    et_commnets_reply_commu_detail_act.text.clear()
                    getPostReplyResponse(0)
                    //focus를 마지막 아이템으로 가도록

                }
            }
        })
    }

    fun onBackBtnClick(view : View){
        finish()
    }
}