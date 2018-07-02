package com.bamilo.modernbamilo.product.descspec.desc


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bamilo.modernbamilo.R
import com.bamilo.modernbamilo.product.descspec.DescSpecWebApi
import com.bamilo.modernbamilo.product.descspec.desc.pojo.DescriptionRow
import com.bamilo.modernbamilo.util.retrofit.RetrofitHelper
import com.bamilo.modernbamilo.util.retrofit.pojo.ResponseWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PRODUCT_ID = "ARG_PRODUCT_ID"

/**
 * A simple [Fragment] subclass.
 * Use the [DescriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DescriptionFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView

    private lateinit var mWebApi: DescSpecWebApi

    private var mProductId: String? = null
    private val mDescriptionRows = ArrayList<DescriptionRow>()

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DescriptionFragment.
         */
        @JvmStatic
        fun newInstance(productId: String) =
                DescriptionFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PRODUCT_ID, productId)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mProductId = it.getString(ARG_PRODUCT_ID)
        }

        mWebApi = RetrofitHelper.makeWebApi(context!!, DescSpecWebApi::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_description, container, false)
        mRecyclerView = rootView.findViewById(R.id.fragmentDescription_recyclerView_descriptionRecyclerView)

        loadDescription()
        initRecyclerView()

        return rootView
    }

    private fun loadDescription() {
        val call = mWebApi.getDescription(mProductId!!)
        call.enqueue(object: Callback<ResponseWrapper<ArrayList<DescriptionRow>>> {

            override fun onResponse(call: Call<ResponseWrapper<ArrayList<DescriptionRow>>>?, response: Response<ResponseWrapper<ArrayList<DescriptionRow>>>?) {
                mDescriptionRows.add(DescriptionRow("دوربین", "نکته", "هوآوی در طراحی میت 10 لایت همانند دیگر مدل\u200Cهای سری میت از استاندارد مشخصی استفاده کرده است. ابعاد این گوشی بزرگ است؛ اما از قاب پشتی صاف و یکدستی بهره می\u200Cبرد. طراحی قاب پشتی آلومینیومی این محصول با دقت بالا انجام شده است و فقط دوربین\u200Cهای پشتی هستند که از سطح گوشی مقداری بیرون زده\u200Cاند. لبه\u200Cهای گوشی نیز به شکل گرد طراحی شده\u200Cاند تا گوشی به نظر جمع\u200Cوجور برسد. حدفاصل میان صفحه\u200Cنمایش شیشه\u200Cای و قاب فلزی در این محصول به\u200Cخوبی پوشانده شده است و جلوه\u200Cی خاصی به گوشی داده است. در قسمت پشتی این گوشی دو نوار آنتن\u200Cدهی دیده می\u200Cشود که به\u200Cخوبی با سطح فلزی و صاف گوشی هماهنگ شده\u200Cاند. قرارگیری فلشی از نوع LED در بالای دوربین\u200Cها کمی غیرعادی به نظر می\u200Cرسد؛ چراکه در اکثر دستگاه\u200Cهای طراحی\u200Cشده توسط هوآوی، این فلش در کنار دوربین قرار می\u200Cگرفت؛ به نظر می\u200Cرسد مقصود هوآوی از این کار، قراردادن تمامی دوربین\u200Cها، فلش و حسگر اثرانگشت گوشی در یک راستا باشد.","https://file.digi-kala.com/digikala/Image/Webstore/ProductReview/P_356804/Reviews/f3a89397-48f0-4977-aab4-34abc02fc299.jpg"))
                mDescriptionRows.add(DescriptionRow("عملکرد", "GL503 لپ\u200Cتاپ قدرتمندی است که با دراختیارداشتن کارت گرافیک مجزای GTX 1070 می\u200Cتواند دل هر گیمری را ببرد.\n", "وشی همراه Mate 10 Lite را می\u200Cتوان یکی از معدود گوشی\u200Cهایی دانست که دارای 4 دوربین است. وجود دو دوربین سلفی سبب شده است که این گوشی بتواند عکس\u200Cهای سلفی با افکت بوکه را در اختیار کاربر قرار دهد. افکت بوکه با مات\u200Cکردن نواحی خارج از فوکوس، جلوه\u200Cی هنری و زیباشناسانه\u200Cای به تصویر می\u200Cبخشد. هم دوربین سلفی وهم دوربین اصلی در پشت گوشی دارای دوربین مکملی با سنسور 2مگاپیکسلی هستند که عمق تصویری لازم برای گرفتن عکس\u200Cهایی با افکت بوکه را فراهم می\u200Cآورند. رابط دوربین نیز دارای دو حالت است؛ حالت اول که هوآوی آن را «Wide aperture mode» نامیده است، برای عکاسی از سوژه\u200Cهایی که در فاصله\u200Cی بیش از 2 متر قرارگرفته\u200Cاند، مناسب است. حالت دوم «Portrait mode» است که افکت بوکه و زیباسازی چهره را به همراه دارد و برای عکاسی از سوژه\u200Cهایی که در فاصله\u200Cی 0.5 تا 2 متری از گوشی قرار گرفته\u200Cاند، مناسب است.","https://file.digi-kala.com/digikala/Image/Webstore/ProductReview/P_356804/Reviews/04248801-f132-44ba-bfd0-e75c6751de3f.jpg"))
                mDescriptionRows.add(DescriptionRow("عملکرد", "نکته", "وشی همراه Mate 10 Lite را می\u200Cتوان یکی از معدود گوشی\u200Cهایی دانست که دارای 4 دوربین است. وجود دو دوربین سلفی سبب شده است که این گوشی بتواند عکس\u200Cهای سلفی با افکت بوکه را در اختیار کاربر قرار دهد. افکت بوکه با مات\u200Cکردن نواحی خارج از فوکوس، جلوه\u200Cی هنری و زیباشناسانه\u200Cای به تصویر می\u200Cبخشد. هم دوربین سلفی وهم دوربین اصلی در پشت گوشی دارای دوربین مکملی با سنسور 2مگاپیکسلی هستند که عمق تصویری لازم برای گرفتن عکس\u200Cهایی با افکت بوکه را فراهم می\u200Cآورند. رابط دوربین نیز دارای دو حالت است؛ حالت اول که هوآوی آن را «Wide aperture mode» نامیده است، برای عکاسی از سوژه\u200Cهایی که در فاصله\u200Cی بیش از 2 متر قرارگرفته\u200Cاند، مناسب است. حالت دوم «Portrait mode» است که افکت بوکه و زیباسازی چهره را به همراه دارد و برای عکاسی از سوژه\u200Cهایی که در فاصله\u200Cی 0.5 تا 2 متری از گوشی قرار گرفته\u200Cاند، مناسب است.","https://file.digi-kala.com/digikala/Image/Webstore/ProductReview/P_523207/Reviews/3cb3e4de-4ba4-4adb-80a6-61eae6b0cf66.jpg"))

                initRecyclerView()
            }

            override fun onFailure(call: Call<ResponseWrapper<ArrayList<DescriptionRow>>>?, t: Throwable?) {
                mDescriptionRows.add(DescriptionRow("دوربین", "نکته", "هوآوی در طراحی میت 10 لایت همانند دیگر مدل\u200Cهای سری میت از استاندارد مشخصی استفاده کرده است. ابعاد این گوشی بزرگ است؛ اما از قاب پشتی صاف و یکدستی بهره می\u200Cبرد. طراحی قاب پشتی آلومینیومی این محصول با دقت بالا انجام شده است و فقط دوربین\u200Cهای پشتی هستند که از سطح گوشی مقداری بیرون زده\u200Cاند. لبه\u200Cهای گوشی نیز به شکل گرد طراحی شده\u200Cاند تا گوشی به نظر جمع\u200Cوجور برسد. حدفاصل میان صفحه\u200Cنمایش شیشه\u200Cای و قاب فلزی در این محصول به\u200Cخوبی پوشانده شده است و جلوه\u200Cی خاصی به گوشی داده است. در قسمت پشتی این گوشی دو نوار آنتن\u200Cدهی دیده می\u200Cشود که به\u200Cخوبی با سطح فلزی و صاف گوشی هماهنگ شده\u200Cاند. قرارگیری فلشی از نوع LED در بالای دوربین\u200Cها کمی غیرعادی به نظر می\u200Cرسد؛ چراکه در اکثر دستگاه\u200Cهای طراحی\u200Cشده توسط هوآوی، این فلش در کنار دوربین قرار می\u200Cگرفت؛ به نظر می\u200Cرسد مقصود هوآوی از این کار، قراردادن تمامی دوربین\u200Cها، فلش و حسگر اثرانگشت گوشی در یک راستا باشد.","https://file.digi-kala.com/digikala/Image/Webstore/ProductReview/P_356804/Reviews/f3a89397-48f0-4977-aab4-34abc02fc299.jpg"))
                mDescriptionRows.add(DescriptionRow("عملکرد", "GL503 لپ\u200Cتاپ قدرتمندی است که با دراختیارداشتن کارت گرافیک مجزای GTX 1070 می\u200Cتواند دل هر گیمری را ببرد.\n", "وشی همراه Mate 10 Lite را می\u200Cتوان یکی از معدود گوشی\u200Cهایی دانست که دارای 4 دوربین است. وجود دو دوربین سلفی سبب شده است که این گوشی بتواند عکس\u200Cهای سلفی با افکت بوکه را در اختیار کاربر قرار دهد. افکت بوکه با مات\u200Cکردن نواحی خارج از فوکوس، جلوه\u200Cی هنری و زیباشناسانه\u200Cای به تصویر می\u200Cبخشد. هم دوربین سلفی وهم دوربین اصلی در پشت گوشی دارای دوربین مکملی با سنسور 2مگاپیکسلی هستند که عمق تصویری لازم برای گرفتن عکس\u200Cهایی با افکت بوکه را فراهم می\u200Cآورند. رابط دوربین نیز دارای دو حالت است؛ حالت اول که هوآوی آن را «Wide aperture mode» نامیده است، برای عکاسی از سوژه\u200Cهایی که در فاصله\u200Cی بیش از 2 متر قرارگرفته\u200Cاند، مناسب است. حالت دوم «Portrait mode» است که افکت بوکه و زیباسازی چهره را به همراه دارد و برای عکاسی از سوژه\u200Cهایی که در فاصله\u200Cی 0.5 تا 2 متری از گوشی قرار گرفته\u200Cاند، مناسب است.","https://file.digi-kala.com/digikala/Image/Webstore/ProductReview/P_356804/Reviews/04248801-f132-44ba-bfd0-e75c6751de3f.jpg"))
                mDescriptionRows.add(DescriptionRow("عملکرد", "نکته", "وشی همراه Mate 10 Lite را می\u200Cتوان یکی از معدود گوشی\u200Cهایی دانست که دارای 4 دوربین است. وجود دو دوربین سلفی سبب شده است که این گوشی بتواند عکس\u200Cهای سلفی با افکت بوکه را در اختیار کاربر قرار دهد. افکت بوکه با مات\u200Cکردن نواحی خارج از فوکوس، جلوه\u200Cی هنری و زیباشناسانه\u200Cای به تصویر می\u200Cبخشد. هم دوربین سلفی وهم دوربین اصلی در پشت گوشی دارای دوربین مکملی با سنسور 2مگاپیکسلی هستند که عمق تصویری لازم برای گرفتن عکس\u200Cهایی با افکت بوکه را فراهم می\u200Cآورند. رابط دوربین نیز دارای دو حالت است؛ حالت اول که هوآوی آن را «Wide aperture mode» نامیده است، برای عکاسی از سوژه\u200Cهایی که در فاصله\u200Cی بیش از 2 متر قرارگرفته\u200Cاند، مناسب است. حالت دوم «Portrait mode» است که افکت بوکه و زیباسازی چهره را به همراه دارد و برای عکاسی از سوژه\u200Cهایی که در فاصله\u200Cی 0.5 تا 2 متری از گوشی قرار گرفته\u200Cاند، مناسب است.","https://file.digi-kala.com/digikala/Image/Webstore/ProductReview/P_523207/Reviews/3cb3e4de-4ba4-4adb-80a6-61eae6b0cf66.jpg"))

                initRecyclerView()
            }

        })
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.itemAnimator = DefaultItemAnimator()
        mRecyclerView.adapter = DescriptionRecyclerAdapter(mDescriptionRows)
    }

}
