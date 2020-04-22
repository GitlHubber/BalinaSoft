package ragalik.brain.balinasoft.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ragalik.brain.balinasoft.data.repository.TypeDataSource
import ragalik.brain.balinasoft.data.repository.TypeDataSourceFactory
import ragalik.brain.balinasoft.data.vo.Content

class TypeViewModel : ViewModel() {
    val adPagedList : LiveData<PagedList<Content>>
    val liveTypeDataSource : LiveData<TypeDataSource>

    init {
        val itemDataSourceFactory = TypeDataSourceFactory()

        liveTypeDataSource = itemDataSourceFactory.livaDataSource

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(TypeDataSource.PAGE_SIZE)
            .build()

        adPagedList = LivePagedListBuilder(itemDataSourceFactory, config)
            .build()
    }
}