package co.edu.udea.compumovil.gr07_20251.udeafood

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.img_product)
        val textName: TextView = view.findViewById(R.id.text_name)
        val textDescription: TextView = view.findViewById(R.id.text_description)
        val textPrice: TextView = view.findViewById(R.id.text_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.textName.text = product.name
        holder.textDescription.text = product.description
        holder.textPrice.text = "$${product.price}"

        Glide.with(holder.imgProduct.context)
            .load(product.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.imgProduct)
    }
}

