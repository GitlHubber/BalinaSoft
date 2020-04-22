package ragalik.brain.balinasoft.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import ragalik.brain.balinasoft.data.repository.TypeDataSource
import ragalik.brain.balinasoft.data.vo.Content

class TypeDataSourceFactory : DataSource.Factory<Int, Content>() {

    val livaDataSource = MutableLiveData<TypeDataSource>()

    override fun create(): DataSource<Int, Content> {
        val dataSource =
            TypeDataSource()
        livaDataSource.postValue(dataSource)

        return dataSource
    }
}