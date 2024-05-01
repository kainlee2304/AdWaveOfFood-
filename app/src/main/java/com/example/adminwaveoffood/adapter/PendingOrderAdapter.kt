package com.example.adminwaveoffood.adapter

import android.content.Context
import android.os.Message
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminwaveoffood.databinding.ActivityCreateUserBinding
import com.example.adminwaveoffood.databinding.PendingOdersItemBinding


class PendingOrderAdapter(
    private val customerNames:ArrayList<String>,
    private val quantity:ArrayList<String>,
    private val foodImage:ArrayList<Int>,
    private val context: Context
):RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PendingOrderViewHolder {
       val binding = PendingOdersItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PendingOrderViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size
    inner class PendingOrderViewHolder(private val binding: PendingOdersItemBinding):RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false
        fun bind(position: Int){
            binding.apply {
                customerName.text = customerNames[position]
                pendingOredarQuantity.text=quantity[position]
                orderfoodImage.setImageResource(foodImage[position])
                orderedAcceptButton.apply {
                    if(!isAccepted){
                        text="Accepted"
                    }else{
                        text="Dispatch"
                    }
                    setOnClickListener{
                        if (!isAccepted){
                            text="Dispatch"
                            isAccepted = true
                            showToast("Order is accepted")
                        }else{
                            customerNames.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            showToast("Order is dispatched ")
                        }

                    }
                }

            }

        }
        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

}