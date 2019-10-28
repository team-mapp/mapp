package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.util.*
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

        iv_test.load(this, "celebs/1_김준현.jpg")
    }

    private fun setupPrintLog() {
        viewModel.printLogData.observe(this, Observer {
            adapter.addItem(TypedItem(TYPE_ITEM, it))
        })
    }

    private fun setupRepositoryTest() {
        viewModel.loadCelebs().observe(this, Observer { resource ->
            resource.onSuccess {
                for (celeb in it!!) {
                    viewModel.printLog("CelebsRepository:loadCelebs", celeb.toString())
                }
            }.onError {
                viewModel.printLog("CelebsRepository:loadCelebs", "Error occurred ($it)")
            }.onLoading {
                viewModel.printLog("CelebsRepository:loadCelebs", "Loading test data...")
            }
        })

        viewModel.loadCelebsOnce().observe(this, Observer { resource ->
            resource.onSuccess {
                for (celeb in it!!) {
                    viewModel.printLog("CelebsRepository:loadCelebsSync", celeb.toString())
                }
            }.onError {
                viewModel.printLog(
                    "CelebsRepository:loadCelebsSync", "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog(
                    "CelebsRepository:loadCelebsSync",
                    "Loading test data..."
                )
            }
        })

        viewModel.loadCeleb("김준현").observe(this, Observer { resource ->
            resource.onSuccess {
                viewModel.printLog("CelebsRepository:loadCeleb", it.toString())
            }.onError {
                viewModel.printLog(
                    "CelebsRepository:loadCeleb", "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog("CelebsRepository:loadCeleb", "Loading test data...")
            }
        })

        viewModel.loadPrograms().observe(this, Observer { resource ->
            resource.onSuccess {
                for (celeb in it!!) {
                    viewModel.printLog("ProgramsRepository:loadPrograms", celeb.toString())
                }
            }.onError {
                viewModel.printLog(
                    "ProgramsRepository:loadPrograms",
                    "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog("ProgramsRepository:loadPrograms", "Loading test data...")
            }
        })

        viewModel.loadProgramsOnce().observe(this, Observer { resource ->
            resource.onSuccess {
                for (celeb in it!!) {
                    viewModel.printLog("ProgramsRepository:loadProgramsSync", celeb.toString())
                }
            }.onError {
                viewModel.printLog(
                    "ProgramsRepository:loadProgramsSync",
                    "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog("ProgramsRepository:loadProgramsSync", "Loading test data...")
            }
        })

        viewModel.loadProgram("맛있는녀석들").observe(this, Observer { resource ->
            resource.onSuccess {
                viewModel.printLog("ProgramsRepository:loadProgram", it.toString())
            }.onError {
                viewModel.printLog(
                    "ProgramsRepository:loadProgram", "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog("ProgramsRepository:loadProgram", "Loading test data...")
            }
        })

        viewModel.loadRestaurants().observe(this, Observer { resource ->
            resource.onSuccess {
                for (celeb in it!!) {
                    viewModel.printLog("RestaurantsRepository:loadRestaurants", celeb.toString())
                }
            }.onError {
                viewModel.printLog(
                    "RestaurantsRepository:loadRestaurants", "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog(
                    "RestaurantsRepository:loadRestaurants",
                    "Loading test data..."
                )
            }
        })

        viewModel.loadRestaurantsOnce().observe(this, Observer { resource ->
            resource.onSuccess {
                for (celeb in it!!) {
                    viewModel.printLog(
                        "RestaurantsRepository:loadRestaurantsSync",
                        celeb.toString()
                    )
                }
            }.onError {
                viewModel.printLog(
                    "RestaurantsRepository:loadRestaurantsSync", "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog(
                    "RestaurantsRepository:loadRestaurantsSync",
                    "Loading test data..."
                )
            }
        })

        viewModel.loadRestaurant("밥한끼").observe(this, Observer { resource ->
            resource.onSuccess {
                viewModel.printLog("RestaurantsRepository:loadRestaurant", it.toString())
            }.onError {
                viewModel.printLog(
                    "RestaurantsRepository:loadRestaurant",
                    "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog(
                    "RestaurantsRepository:loadRestaurant",
                    "Loading test data..."
                )
            }
        })

        viewModel.loadCelebRelations("rnDrhqPhwwmetV32b6Sm").observe(this, Observer { resource ->
            resource.onSuccess {
                viewModel.printLog(
                    "CelebRelationsRepository:loadCelebRelations",
                    it.toString()
                )
            }.onError {
                viewModel.printLog(
                    "CelebRelationsRepository:loadCelebRelations",
                    "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog(
                    "CelebRelationsRepository:loadCelebRelations",
                    "Loading test data..."
                )
            }
        })

        viewModel.loadProgramRelations("PrkVtuklSufTq34KiJAP").observe(this, Observer { resource ->
            resource.onSuccess {
                viewModel.printLog(
                    "ProgramRelationsRepository:loadProgramRelations",
                    it.toString()
                )
            }.onError {
                viewModel.printLog(
                    "ProgramRelationsRepository:loadProgramRelations",
                    "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog(
                    "ProgramRelationsRepository:loadProgramRelations",
                    "Loading test data..."
                )
            }
        })

        viewModel.loadCelebRelationsByName("이영자")?.observe(this, Observer { resource ->
            resource.onSuccess {
                viewModel.printLog(
                    ":loadCelebRelationsByName",
                    it.toString()
                )
            }.onError {
                viewModel.printLog(
                    ":loadCelebRelationsByName",
                    "Error occurred ($it)"
                )
            }.onLoading {
                viewModel.printLog(
                    ":loadCelebRelationsByName",
                    "Loading test data..."
                )
            }
        })

        viewModel.loadCelebWithRelations("이영자").observe(this, Observer {
            viewModel.printLog(":loadCelebWithRelations", it.toString())
        })

        viewModel.loadRestaurantsFromCelebName("이영자").observe(this, Observer {
            for (restaurant in it.data!!) {
                viewModel.printLog(":loadRestaurantsFromCelebName", restaurant.toString())
            }
        })
    }

    companion object {
        const val TYPE_HEADER = 100
        const val TYPE_ITEM = 101
        const val TAG = "MainActivity"
    }
}
