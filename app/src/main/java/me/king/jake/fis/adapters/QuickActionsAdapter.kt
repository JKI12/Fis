package me.king.jake.fis.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import me.king.jake.fis.R
import me.king.jake.fis.models.QuickAction

class QuickActionsAdapter(private val actions: ArrayList<QuickAction>) : RecyclerView.Adapter<QuickActionsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quick_action_chip, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return actions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = actions[position]

        holder.apply {
            chip.text = item.text
            chip.setOnClickListener {
                if (item.onClick != null) {
                    item.onClick.invoke()
                }
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chip: Chip = view.findViewById(R.id.qa_chip)
    }
}