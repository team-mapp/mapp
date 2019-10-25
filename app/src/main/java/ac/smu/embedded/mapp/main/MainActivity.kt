package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.model.Status
import ac.smu.embedded.mapp.util.BaseRecyclerAdapter
import ac.smu.embedded.mapp.util.TypedItem
import ac.smu.embedded.mapp.util.getViewModelFactory
import ac.smu.embedded.mapp.util.recyclerAdapter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_test.view.*

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }

    private lateinit var adapter: BaseRecyclerAdapter<TypedItem<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        setupPrintLog()
        setupRepositoryTest()
    }

    private fun initView() {
        adapter =
            recyclerAdapter(
                mapOf(
                    TYPE_HEADER to R.layout.item_header,
                    TYPE_ITEM to R.layout.item_test
                ), mutableListOf(
                    TypedItem(TYPE_HEADER, "Test")
                )
            ) { view, item ->
                if (item.type == TYPE_HEADER) {
                    view.tv_header.text = item.item
                } else if (item.type == TYPE_ITEM) {
                    view.tv_title.text = item.item
                }
            }

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
    }

    private fun setupPrintLog() {
        viewModel.printLogData.observe(this, Observer {
            adapter.addItem(TypedItem(TYPE_ITEM, it))
        })
    }

    private fun setupRepositoryTest() {
        viewModel.loadCelebs().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    for (celeb in it.data!!) {
                        viewModel.printLog("CelebsRepository:loadCelebs", celeb.toString())
                    }
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "CelebsRepository:loadCelebs",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog("CelebsRepository:loadCelebs", "Loading test data...")
                }
            }
        })

        viewModel.loadCelebsSync().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    for (celeb in it.data!!) {
                        viewModel.printLog("CelebsRepository:loadCelebsSync", celeb.toString())
                    }
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "CelebsRepository:loadCelebsSync",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog("CelebsRepository:loadCelebsSync", "Loading test data...")
                }
            }
        })

        viewModel.loadCeleb("김준현").observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    viewModel.printLog("CelebsRepository:loadCeleb", it.data.toString())
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "CelebsRepository:loadCeleb",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog("CelebsRepository:loadCeleb", "Loading test data...")
                }
            }
        })

        viewModel.loadPrograms().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    for (celeb in it.data!!) {
                        viewModel.printLog("ProgramsRepository:loadPrograms", celeb.toString())
                    }
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "ProgramsRepository:loadPrograms",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog("ProgramsRepository:loadPrograms", "Loading test data...")
                }
            }
        })

        viewModel.loadProgramsSync().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    for (celeb in it.data!!) {
                        viewModel.printLog("ProgramsRepository:loadProgramsSync", celeb.toString())
                    }
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "ProgramsRepository:loadProgramsSync",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog(
                        "ProgramsRepository:loadProgramsSync",
                        "Loading test data..."
                    )
                }
            }
        })

        viewModel.loadProgram("맛있는녀석들").observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    viewModel.printLog("ProgramsRepository:loadProgram", it.data.toString())
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "ProgramsRepository:loadProgram",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog("ProgramsRepository:loadProgram", "Loading test data...")
                }
            }
        })

        viewModel.loadRestaurants().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    for (celeb in it.data!!) {
                        viewModel.printLog(
                            "RestaurantsRepository:loadRestaurants",
                            celeb.toString()
                        )
                    }
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "RestaurantsRepository:loadRestaurants",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog(
                        "RestaurantsRepository:loadRestaurants",
                        "Loading test data..."
                    )
                }
            }
        })

        viewModel.loadRestaurantsSync().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    for (celeb in it.data!!) {
                        viewModel.printLog(
                            "RestaurantsRepository:loadRestaurantsSync",
                            celeb.toString()
                        )
                    }
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "RestaurantsRepository:loadRestaurantsSync",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog(
                        "RestaurantsRepository:loadRestaurantsSync",
                        "Loading test data..."
                    )
                }
            }
        })

        viewModel.loadRestaurant("밥한끼").observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    viewModel.printLog("RestaurantsRepository:loadRestaurant", it.data.toString())
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "RestaurantsRepository:loadRestaurant",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog(
                        "RestaurantsRepository:loadRestaurant",
                        "Loading test data..."
                    )
                }
            }
        })

        viewModel.loadCelebRelations("rnDrhqPhwwmetV32b6Sm").observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    viewModel.printLog(
                        "CelebRelationsRepository:loadCelebRelations",
                        it.data.toString()
                    )
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "CelebRelationsRepository:loadCelebRelations",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog(
                        "CelebRelationsRepository:loadCelebRelations",
                        "Loading test data..."
                    )
                }
            }
        })

        viewModel.loadProgramRelations("PrkVtuklSufTq34KiJAP").observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    viewModel.printLog(
                        "ProgramRelationsRepository:loadProgramRelations",
                        it.data.toString()
                    )
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        "ProgramRelationsRepository:loadProgramRelations",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog(
                        "ProgramRelationsRepository:loadProgramRelations",
                        "Loading test data..."
                    )
                }
            }
        })

        viewModel.loadCelebRelationsByName("이영자")?.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    viewModel.printLog(
                        ":loadCelebRelationsByName",
                        it.data.toString()
                    )
                }
                Status.ERROR -> {
                    viewModel.printLog(
                        ":loadCelebRelationsByName",
                        "Error occurred (${it.error})"
                    )
                }
                Status.LOADING -> {
                    viewModel.printLog(
                        ":loadCelebRelationsByName",
                        "Loading test data..."
                    )
                }
            }
        })

        viewModel.loadCelebWithRelations("이영자")?.observe(this, Observer {
            viewModel.printLog(":loadCelebWithRelations", it.toString())
        })
    }

    companion object {
        const val TYPE_HEADER = 100
        const val TYPE_ITEM = 101
        const val TAG = "MainActivity"
    }
}
