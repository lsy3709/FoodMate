package com.example.foodmate3.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.foodmate3.databinding.ItemBoardBinding
import com.example.foodmate3.model.BoardDto


class BoardViewHolder(val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root)

class BoardAdapter(private val context: Context, private val boardList: MutableList<BoardDto>) :
    RecyclerView.Adapter<BoardViewHolder>() {

    override fun getItemCount(): Int {
        return boardList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val binding = holder.binding
        val board = boardList?.get(position)

        binding.BoardTitle.text = board?.title
        binding.UserNickname.text = board?.userNicname
        binding.BarName.text = board?.barName
        binding.UserCount.text = board?.memberCount
        binding.MeetDate.text = board?.meetdate?.toString()
        binding.RegDate.text = board?.regdate?.toString()

        val urlImg = board?.barImg

        Glide.with(context)
            .asBitmap()
            .load(urlImg)
            .into(object : CustomTarget<Bitmap>(200, 200) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.BoardImg.setImageBitmap(resource)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    // 이미지 로딩이 취소되었을 때의 동작을 정의하려면 여기에 코드를 추가하세요.
                }
            })
    }
    fun setData(data: List<BoardDto>?) {
        boardList.clear()
        if (data != null) {
            boardList.addAll(data)
        }
        notifyDataSetChanged()
    }
}