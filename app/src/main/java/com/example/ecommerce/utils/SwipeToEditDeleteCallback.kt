package com.example.ecommerce

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeToEditDeleteCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {

    private val editIcon = ContextCompat.getDrawable(context, R.drawable.baseline_edit_white)
    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.delete_white)
    private val intrinsicWidth = editIcon!!.intrinsicWidth
    private val intrinsicHeight = editIcon!!.intrinsicHeight
    private val backgroundEdit = ColorDrawable(Color.parseColor("#24AE05"))
    private val backgroundDelete = ColorDrawable(Color.parseColor("#FF0000"))
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (viewHolder.adapterPosition == 10) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, itemView.left + dX, itemView.top.toFloat(), itemView.left.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        when {
            dX > 0 -> { // Swiping to the right (Edit)
                backgroundEdit.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
                backgroundEdit.draw(c)

                val editIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val editIconMargin = (itemHeight - intrinsicHeight)
                val editIconLeft = itemView.left + editIconMargin - intrinsicWidth
                val editIconRight = itemView.left + editIconMargin
                val editIconBottom = editIconTop + intrinsicHeight

                editIcon!!.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
                editIcon.draw(c)
            }
            dX < 0 -> { // Swiping to the left (Delete)
                backgroundDelete.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                backgroundDelete.draw(c)

                val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
                val deleteIconMargin = (itemHeight - intrinsicHeight)
                val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
                val deleteIconRight = itemView.right - deleteIconMargin
                val deleteIconBottom = deleteIconTop + intrinsicHeight

                deleteIcon!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                deleteIcon.draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}
