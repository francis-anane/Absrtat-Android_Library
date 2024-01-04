package com.abstratsystems.abstratlibrary


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView Adapter for displaying a list of items of type DATATYPE.
 *
 * @property context The context in which the adapter is used.
 * @property listData The list of data to be display.
 * @property viewsLayoutResFileId The id of the layout resource file containing the view items to
 *              be displayed in the recyclerView.
 * @property viewsLayoutParentId The id of the parent layout for the various view items
 *              to be displayed in the recyclerView
 * @property onBind Function to bind data to the item views.
 */
class AbstratRecyclerViewAdapter<DATATYPE, VIEWTYPE>(
    private val context: Context,
    private var listData: ArrayList<DATATYPE>,
    private val viewsLayoutResFileId: Int,
    private val viewsLayoutParentId: Int,
    private val onBind: (View, DATATYPE) -> Unit
) : RecyclerView.Adapter<AbstratRecyclerViewAdapter<DATATYPE, VIEWTYPE>.ViewHolder>() {

    /**
     * ViewHolder class responsible for holding references to the item views.
     *
     * @param itemView The root view of the item layout.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize The parent layout of the views to be shown
        private val viewItemsParent: VIEWTYPE = itemView.findViewById(viewsLayoutParentId)

        init {

        }

        /**
         * Bind data of type DATATYPE to the item view.
         *
         * @param data The data to bind to the item view.
         */
        fun bind(data: DATATYPE) {
            // Call the provided binding function to set data on the item view
            onBind(itemView, data)
        }
    }

    /**
     * Create a new ViewHolder by inflating the item view layout.
     *
     * @param parent The parent ViewGroup in which the ViewHolder will be created.
     * @param viewType The type of the view to be created.
     * @return A new ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item view layout
        val itemView = LayoutInflater.from(parent.context).inflate(viewsLayoutResFileId, parent, false)
        return ViewHolder(itemView)
    }

    /**
     * Bind data to the item views at a given position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item in the data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Call the ViewHolder's bind method to bind data to the item view
        holder.bind(listData[position])
    }

    /**
     * Get the number of items in the data set.
     *
     * @return The number of items in the data set.
     */
    override fun getItemCount(): Int {
        return listData.size
    }

    /**
     * Update the list of items and notify the adapter of the data change.
     *
     * @param newData The new list of items to display.
     */
    fun updateData(newData: ArrayList<DATATYPE>) {
        listData = newData
        notifyDataSetChanged()
    }

}