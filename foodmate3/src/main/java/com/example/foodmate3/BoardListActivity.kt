package com.example.foodmate3

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodmate3.adapter.BoardAdapter
import com.example.foodmate3.controller.BoardController
import com.example.foodmate3.controller.SharedPreferencesUtil
import com.example.foodmate3.databinding.ActivityBoardListBinding
import com.example.foodmate3.fragment.CalendarFragment
import com.example.foodmate3.fragment.ChatFragment
import com.example.foodmate3.fragment.HomeFragment
import com.example.foodmate3.fragment.MyFragment
import com.example.foodmate3.model.BoardDto
import com.example.foodmate3.network.RetrofitBuilder
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class BoardListActivity : AppCompatActivity() {

    private val TAG: String = "BoardListActivity"
    private var bottomNavigationView: BottomNavigationView? = null

    private lateinit var binding: ActivityBoardListBinding
    private lateinit var boardService: BoardController

    private lateinit var recyclerView: RecyclerView
    private lateinit var boardAdapter: BoardAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar) // 툴바 설정

        boardService = RetrofitBuilder.BoardListService()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this@BoardListActivity)

        val boardList: MutableList<BoardDto> = mutableListOf()
        boardAdapter = BoardAdapter(this@BoardListActivity, boardList)
        recyclerView.adapter = boardAdapter

        getBoardList(boardService)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView!!.setOnNavigationItemSelectedListener(TabSelectedListener())

        // 플러스 버튼 클릭시 팝업 메뉴
        binding.plus.setOnClickListener { showPopupMenu(binding.plus) }

        val isLoggedIn = SharedPreferencesUtil.checkLoggedIn(this)
        Log.d(TAG, "세션 유지 상태: $isLoggedIn")
    }


    private fun SettingListener() {
        // 선택 리스너 등록
        bottomNavigationView!!.setOnNavigationItemSelectedListener(TabSelectedListener())
    }


    // 메뉴 리소스 XML의 내용을 앱바(App Bar)에 반영
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)

        // MainActivity의 onCreateOptionsMenu 함수 내에서 호출하여 세션 유지 상태를 확인하는 예시입니다.
        val isLoggedIn = SharedPreferencesUtil.checkLoggedIn(this)
        val loginMenuItem = menu.findItem(R.id.login)
        val logoutMenuItem = menu.findItem(R.id.logout)

        if (isLoggedIn) {
            // 로그인 상태인 경우
            loginMenuItem.isVisible = false // 로그인 메뉴 숨기기
            logoutMenuItem.isVisible = true // 로그아웃 메뉴 보이기
        } else {
            // 로그아웃 상태인 경우
            loginMenuItem.isVisible = true // 로그인 메뉴 보이기
            logoutMenuItem.isVisible = false // 로그아웃 메뉴 숨기기
        }
        return true
    }
    //앱바(App Bar)에 표시된 액션 또는 오버플로우 메뉴가 선택되면
    //액티비티의 onOptionsItemSelected() 메서드가 호출
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.login) {
            // 사용자가 이미 로그인 화면에 있으므로 다시 이동할 필요가 없습니다.
            return true
        } else if (itemId == R.id.logout) {
            // 로그아웃 처리
            SharedPreferencesUtil.setLoggedIn(this, false) // 로그인 상태를 false로 설정합니다.
            invalidateOptionsMenu() // 옵션 메뉴를 다시 그리도록 호출합니다.

            // MainActivity2(로그인 화면)로 이동합니다.
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    internal inner class TabSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener {
        @SuppressLint("NonConstantResourceId")
        override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
            val itemId = menuItem.itemId
            if (itemId == R.id.tab_home) {
                // 홈 프래그먼트를 표시하도록 변경하거나 필요한 대상 프래그먼트로 수정해주세요
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, HomeFragment())
                    .commit()

                // BarListActivity 화면 숨기기
                binding.recyclerView.visibility = View.GONE

                return true
            } else if (itemId == R.id.tab_chat) {
                // 채팅 프래그먼트를 표시하도록 변경하거나 필요한 대상 프래그먼트로 수정해주세요
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ChatFragment())
                    .commit()

                // BarListActivity 화면 숨기기
                binding.recyclerView.visibility = View.GONE

                return true
            } else if (itemId == R.id.tab_calendar) {
                // 캘린더 프래그먼트를 표시하도록 변경하거나 필요한 대상 프래그먼트로 수정해주세요
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, CalendarFragment())
                    .commit()

                // BarListActivity 화면 숨기기
                binding.recyclerView.visibility = View.GONE

                return true
            } else if (itemId == R.id.tab_my) {
                // 마이 프래그먼트를 표시하도록 변경하거나 필요한 대상 프래그먼트로 수정해주세요
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, MyFragment())
                    .commit()

                // BarListActivity 화면 숨기기
                binding.recyclerView.visibility = View.GONE

                return true
            }
            return false
        }
    }
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.board_menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == R.id.writing) {
                // 모집글 작성 메뉴 클릭 시 처리할 로직 작성
                // 예: BoardInsertActivity로 이동
                val intent = Intent(this@BoardListActivity, BoardInsert::class.java)
                startActivity(intent)
                return@OnMenuItemClickListener true
            } else if (itemId == R.id.mywriteview) {
                // 내가 작성한 글 보기 메뉴 클릭 시 처리할 로직 작성
                // 예: MyWritePage로 이동
                val intent = Intent(this@BoardListActivity, MyWritePage::class.java)
                startActivity(intent)
                return@OnMenuItemClickListener true
            }else if (itemId == R.id.barlist) {
                // 내가 작성한 글 보기 메뉴 클릭 시 처리할 로직 작성
                // 예: MyWritePage로 이동
                val intent = Intent(this@BoardListActivity, BarListActivity::class.java)
                startActivity(intent)
                return@OnMenuItemClickListener true
            }
            false
        })
        popupMenu.show()
    }

    private fun getBoardList(boardService: BoardController) {
        val foodListCall: Call<List<BoardDto>> = boardService.getAllBoard()

        foodListCall.enqueue(object : Callback<List<BoardDto>> {
            override fun onResponse(call: Call<List<BoardDto>>, response: Response<List<BoardDto>>) {
                if (response.isSuccessful) {
                    val boardListResponse = response.body()
                    Log.d("lsy","test: " + boardListResponse)
                    boardListResponse?.let {
                        boardAdapter.setData(it)
                    }
                } else {
                    Log.e("BoardList", "Error: ${response.code()}")
                }
            }


            override fun onFailure(call: Call<List<BoardDto>>, t: Throwable) {
                if (t is IOException) {
                    Log.e("BoardList", "Network Error: ${t.message}")
                } else if (t is HttpException) {
                    Log.e("BoardList", "HTTP Error: ${t.code()}")
                } else {
                    Log.e("BoardList", "Error: ${t.message}")
                }
            }
        })
    }
}