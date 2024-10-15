package com.az.elib.presentation.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.az.elib.R
import com.az.elib.databinding.FragmentLibraryBinding
import com.az.elib.domain.interfaces.OnCourseClickedListener
import com.az.elib.domain.models.Course
import com.az.elib.presentation.adapters.CourseAdapter
import com.az.elib.presentation.util.RecyclerViewSlideAnimator
import com.az.elib.presentation.ui.activities.FilesPreviewActivity
import com.az.elib.util.Constants
import com.google.android.material.tabs.TabLayout


class LibraryFragment : Fragment(), OnCourseClickedListener {

    companion object {
        private const val CP1S1 = "1cp1sIsShowing"
        private const val CP1S2 = "1cp2sIsShowing"
        private const val CP2S1 = "2cp1sIsShowing"
        private const val CP2S2 = "2cp2sIsShowing"
        private const val CS1S1 = "1cs1sIsShowing"
        private const val CS1S2 = "1cs2sIsShowing"
        private const val CS2S1 = "2cs1sIsShowing"
        private const val CS2S2AI = "2cs2AiIsShowing"
        private const val CS2S2CS = "2cs2CsIsShowing"
        private const val CS3S1AI = "3cs1AiIsShowing"
        private const val CS3S1CS = "3cs1CsIsShowing"
    }

    private lateinit var binding: FragmentLibraryBinding
    private lateinit var mAdapter: CourseAdapter
    private val rvSlideAnimator = RecyclerViewSlideAnimator()


    private val showState = hashMapOf(
        "1cp1sIsShowing" to false,
        "1cp2sIsShowing" to false,
        "2cp1sIsShowing" to false,
        "2cp2sIsShowing" to false,
        "1cs1sIsShowing" to false,
        "1cs2sIsShowing" to false,
        "2cs1sIsShowing" to false,
        "2cs2AiIsShowing" to false,
        "2cs2CsIsShowing" to false,
        "3cs1AiIsShowing" to false,
        "3cs1CsIsShowing" to false

    )

    private val preparatoryCycleCourses: List<Course> = listOf(
        // Year 1, Semester 1
        Course(
            "Algorithms & static data structures 1",
            1,
            1,
            "1NjGYXf91V6XgcPrewrhmpGpzFPrK2Q4X",
            R.drawable.im_algorithms,
            "Generalized"
        ),
        Course(
            "Computer Architecture 1",
            1,
            1,
            "1Xx674BLZGwLKQIjvtgGkAOhQ3LzvHDo7",
            R.drawable.im_architecture,
            "Generalized"
        ),
        Course(
            "Introduction to operating system 1",
            1,
            1,
            "10GjY-klNg5hat9o-xwZFpN47H1abcGKr",
            R.drawable.im_os,
            "Generalized"
        ),
        Course(
            "Mathematical analysis 1",
            1,
            1,
            "10mtSP4oyyrVvcZmra9lqjzBeMGAGEUjB",
            R.drawable.im_analyse,
            "Generalized"
        ),
        Course(
            "Algebra 1",
            1,
            1,
            "1aVzI8Ti--amStcB7Gmv74ACE6O2YXcJO",
            R.drawable.im_algebra,
            "Generalized"
        ),
        Course(
            "Electricity",
            1,
            1,
            "1e76lm0ITWnxV4Yyga8AshHTqb5-cZVpe",
            R.drawable.im_electricity,
            "Generalized"
        ),
        Course(
            "English 1",
            1,
            1,
            "1eXv1bOSkzdlLYEUD4x9BBUMxWk8TGFQV",
            R.drawable.im_english,
            "Generalized"
        ),
        Course(
            "Office automation & website",
            1,
            1,
            "1R7U5iWb8rhl1-oLy_rpCQSB89s_iqain",
            R.drawable.im_bureautique,
            "Generalized"
        ),

        // Year 1, Semester 2
        Course(
            "Algorithms & dynamic data structures",
            1,
            2,
            "1btx-IdLTWQHfbQdVufFKI-k5dVPskHqr",
            R.drawable.im_algorithms,
            "Generalized"
        ),
        Course(
            "Introduction to operating system 2",
            1,
            2,
            "1cZxDbBtaMyIz3KxnJsVoRW6z094Q21tx",
            R.drawable.im_os,
            "Generalized"
        ),
        Course(
            "Mathematical analysis 2",
            1,
            2,
            "1rzwl_iDNDgAi3RQyJx3fwZvOeL1hqCUs",
            R.drawable.im_analyse,
            "Generalized"
        ),
        Course(
            "Algebra 2",
            1,
            2,
            "1LR6-1ltrEQkHE79LeNxvsY_31LZfeduO",
            R.drawable.im_algebra,
            "Generalized"
        ),
        Course(
            "Mechanics of point",
            1,
            2,
            "1aONW0dxauHM0HPma2LLdeAC6UVwSReql",
            R.drawable.im_mec_point,
            "Generalized"
        ),
        Course(
            "Fundamental electronic 1",
            1,
            2,
            "10v3vM1pMFwIMTZZW3uyR7Sa_1aYZk8e1",
            R.drawable.im_electronique,
            "Generalized"
        ),
        Course(
            "Expression techniques",
            1,
            2,
            "1uAgDdxvYK3prknmKnG3u8aKw2-ESmKBW",
            R.drawable.im_expr_tech,
            "Generalized"
        ),
        Course(
            "English 2",
            1,
            2,
            "15Fch_GGyPS20z676L4dHFZLgKXGe4Jft",
            R.drawable.im_english,
            "Generalized"
        ),

        // Year 2, Semester 1
        Course(
            "Files structure & data structure",
            2,
            1,
            "1HaKGklHvsZbYigwEve0BAvHa42G7dfyZ",
            R.drawable.im_algorithms,
            "Generalized"
        ),
        Course(
            "Computer Architecture 2",
            2,
            1,
            "1x0iQfULhfe4rqPg9pCsr3tOOprHnBDrP",
            R.drawable.im_architecture,
            "Generalized"
        ),
        Course(
            "Mathematical analysis 3",
            2,
            1,
            "1AmWEwOCPoMiZG81pUL0WDzCTdf8q-sv1",
            R.drawable.im_analyse,
            "Generalized"
        ),
        Course(
            "Algebra 3",
            2,
            1,
            "1-fgQSJhgh9iwq-AoSeR_DXk_KYR0F0_S",
            R.drawable.im_algebra,
            "Generalized"
        ),
        Course(
            "Fundamental electronic 2",
            2,
            1,
            "1UDvFdXgbYu9-STnUKnHWJiYNfMCnzvTz",
            R.drawable.im_electronique,
            "Generalized"
        ),
        Course(
            "Probabilities & Statistics 1",
            2,
            1,
            "1SGACCSZUTuxAYk3BZvVKN8d5Evvt4dKA",
            R.drawable.im_proba_and_stat,
            "Generalized"
        ),
        Course(
            "Business economics",
            2,
            1,
            "1Pt1BQFkSAvdfMm_Hla41LT_v6ja8-DLF",
            R.drawable.im_business_economics,
            "Generalized"
        ),

        // Year 2, Semester 2
        Course("Object-oriented programming", 2, 2, "1NmzoC_xHYnGvs3czrY_gcLAdE3KXB06_", R.drawable.im_oop, "Generalized"),
        Course(
            "Introduction to information system",
            2,
            2,
            "1glXlIRelJqO7ndk6YdV405xzmPnrP4o-",
            R.drawable.im_info_sys,
            "Generalized"
        ),
        Course("Mathematical analysis 4", 2, 2, "1w96bWjfgV7NuY1eNf9frNWIWOqB3_4jD", R.drawable.im_analyse, "Generalized"),
        Course("Mathematical logic", 2, 2, "1hAjY-PVvKD-X70WWCOuHgSX6Wo9gr9T5", R.drawable.im_math_logic, "Generalized"),
        Course(
            "Multidisciplinary project",
            2,
            2,
            "",
            R.drawable.im_multidisciplinary_project,
            "Generalized"
        ),
        Course(
            "Optics & electromagnetic waves",
            2,
            2,
            "16vFojAbUKPxgHo6UOrreTmLAJ7n3Bley",
            R.drawable.im_optics_and_elec_waves,
            "Generalized"
        ),
        Course(
            "Probabilities & Statistics 2",
            2,
            2,
            "1_WuIsJKOyaI4jxYvBza5z_3zbk5mGlpc",
            R.drawable.im_proba_and_stat,
            "Generalized"
        )
    )
    private val superiorCycleCourses: List<Course> = listOf(
        // Year 3, Semester 1
        Course("Operating system", 3, 1, "1fNbbz7SwJWN-DF8rbaIhuOSgyirIf052", R.drawable.im_sup_os, "General"),
        Course("Networks 1", 3, 1, "1VNJ2c-nWqO5yf2t4wPXLpCwQJUnJYAgq", R.drawable.im_network, "General"),
        Course("Data base", 3, 1, "1OsRs44DgcuSNQxe5RqHfy2NUvd9YxgaI", R.drawable.im_db, "General"),
        Course("Software engineering", 3, 1, "1fNbbz7SwJWN-DF8rbaIhuOSgyirIf052", R.drawable.im_se, "General"),
        Course("Operational Research 1", 3, 1, "", R.drawable.im_res_oper, "General"),
        Course("Random Processes & Queues", 3, 1, "15_bEtGMUSAS3QWltkT83ry7c1SHoukrk", R.drawable.im_random_proces, "General"),
        Course("Language theory", 3, 1, "1kBxGCdV-YDCF07FiYI6O_7nKFilZKYYM", R.drawable.im_language_theory, "General"),
        Course("Technical English 1", 3, 1, "1T6dgCgr6gTWBQxhzcREc-vfjVVkZRctO", R.drawable.im_english, "General"),

        // Year 3, Semester 2
        Course(
            "Distributed Architecture & intensive Computing",
            3,
            2,
            "1gbbdMyysYecON-vBKHw0GWKqQKCz2Tyl",
            R.drawable.im_distr_process,
            "General"
        ),
        Course("Networks 2", 3, 2, "1kB24i4RgdgtJLPTxTg53tiB6_zEL555_", R.drawable.im_network, "General"),
        Course("Artificial intelligence", 3, 2, "1GkqbZnsSp1Tm06M6GocAB2ZUcBDB5KRJ", R.drawable.im_ai, "General"),
        Course("IT security", 3, 2, "1kB24i4RgdgtJLPTxTg53tiB6_zEL555_", R.drawable.im_it_security, "General"),
        Course("Operational Research 2", 3, 2, "1dMFIR0DIHvaHjzsFnp5hd0Jk2V1PDRmi", R.drawable.im_res_oper, "General"),
        Course("Formal methods", 3, 2, "1cai2TKRvhjf25o1s0NyUJWfnFUmzaBu-", R.drawable.im_automate, "General"),
        Course("Numerical analysis", 3, 2, "1u5uiH7eBK7nx14Cpd9fXPRrevtG3VpfZ", R.drawable.im_analyse, "General"),
        Course(
            "Entrepreneurship & digital startups",
            3,
            2,
            "1lHb5-4mV6LSVoIDlRksDe4mdBOttJvon",
            R.drawable.im_startup,
            "General"
        ),

        // Year 4, Semester 1
        Course("Fundamentals of data science", 4, 1, "1MVgH-iAOCwVvZmgrWL7QmJGGSXTxUWh-", R.drawable.im_datascience, "General"),
        Course("Complexity of issues", 4, 1, "1yv2pwROItGCLsaS0KH_XY6HI1vmMbCFP", R.drawable.im_problem_complexity, "General"),
        Course("Advanced databases", 4, 1, "179j-Hm6r-VUvBySpT5PK8YeQLJvqr71q", R.drawable.im_db, "General"),
        Course("Software engineering", 4, 1, "1qtcwAEbVUtOq2pjq_fwC0vHSGWmit1H1", R.drawable.im_se, "General"),
        Course("Cloud computing", 4, 1, "13MkYEO6oOWn6A21J6azAjMBBdrvjaxR-", R.drawable.im_cloud_computing, "General"),
        Course("Project management", 4, 1, "1a2RptJAO82rxg2RlHjD8dD1WzJjShWwC", R.drawable.im_project_managment, "General"),
        Course("Data analysis", 4, 1, "1He8Exghy0IaVm4SOdSpSAyVqpcNnfR5F", R.drawable.im_data_analysis, "General"),
        Course("Technical English 2", 4, 1, "1rDd0een-rBmtlcCshShURtL8Cvtjz71l", R.drawable.im_english, "General"),

        // Year 4, Semester 2 - AI Specialization
        Course("Machine Learning", 4, 2, "1BIrDHVq7E0CQKuF6OZ7n4kZIWXsS2vUQ", R.drawable.im_ml, "AI"),
        Course("Data warehouse & big data", 4, 2, "1MaXITO3ryy1qyBwswJXJR6ZEZaQosFPu", R.drawable.im_bigdata, "AI"),
        Course("Distributed databases", 4, 2, "1lwBE1b7cYG0lr1ombJpgpqeVoUcx_00y", R.drawable.im_distributed_db, "AI"),
        Course("Knowledge Engineering", 4, 2, "1zaEZ29ALC-kaZZ71GSe26XexGaIGYQFC", R.drawable.im_knwoledge_engineering, "AI"),
        Course("Advanced statistics", 4, 2, "1TrY8irZag2Y9IifOQMDKBFuPc9tMToO6", R.drawable.im_advanced_statistics, "AI"),
        Course("Image processing base", 4, 2, "1C6ivEjyr50OnaKLBXQQGoQDd9DsA_6ls", R.drawable.im_image_processing, "AI"),
        Course("Time series", 4, 2, "1gKBBm3hDF7Bd5EGx9paMp-EGgSmpQEmU", R.drawable.im_time_series, "AI"),
        Course("Digital technologies in organizations", 4, 2, "1GZ3Y556ENywaIi0LqD0iEuguDzKpewYy", R.drawable.im_digital_tech, "AI"),

        // Year 4, Semester 2 - Cyber Security Specialization
        Course("Machine Learning", 4, 2, "15rsCw4f9GGHN1rBk2f4WNdQl-AtqtqwB", R.drawable.im_ml, "CS"),
        Course("Formal methods for security", 4, 2, "1OVAmuQG1isFO1lUWBKUuUXXOREMiksDZ", R.drawable.im_it_security, "CS"),
        Course("Advanced Cryptography", 4, 2, "1c6ZZUcLmgBbl_N6tVW4wyUaOVHDkQNpG", R.drawable.im_it_security, "CS"),
        Course(
            "System & network administration",
            4,
            2,
            "1bmml9vrRHki1D_G9WV0NRGvcd00cfwWi",
            R.drawable.im_network_administration,
            "CS"
        ),
        Course("Network security", 4, 2, "11DYwxPWtM-iQqnn7O9vcuy4e-RzIEuUo", R.drawable.im_network_security, "CS"),
        Course("Operating system security", 4, 2, "1blKjcgZ3k-YUds5qnWoKpFzlKICOtzM6", R.drawable.im_os_security, "CS"),
        Course("Information systems security audit", 4, 2, "", R.drawable.im_sec_audit, "CS"),
        Course("Biometrics", 4, 2, "1mOGjuPTpeZ3T74iFm-JVbOQa8Ao3LPTY", R.drawable.im_biometrics, "CS"),

        // Year 5, Semester 1 - AI Specialization
        Course("Deep learning", 5, 1, "", R.drawable.im_architecture, "AI"),
        Course("Reinforcement learning", 5, 1, "", R.drawable.im_architecture, "AI"),
        Course(
            "Pattern recognition for image analysis",
            5,
            1,
            "",
            R.drawable.im_architecture,
            "AI"
        ),
        Course("Automatic language processing", 5, 1, "", R.drawable.im_architecture, "AI"),
        Course("Business intelligence", 5, 1, "", R.drawable.im_architecture, "AI"),
        Course("NoSQL databases", 5, 1, "", R.drawable.im_architecture, "AI"),
        Course("Ethics in AI", 5, 1, "", R.drawable.im_architecture, "AI"),

        // Year 5, Semester 1 - Cyber Security Specialization
        Course("Software Security", 5, 1, "", R.drawable.im_architecture, "CS"),
        Course("Infrastructure Security", 5, 1, "", R.drawable.im_architecture, "CS"),
        Course("Trust management", 5, 1, "", R.drawable.im_architecture, "CS"),
        Course("Data anonymization", 5, 1, "", R.drawable.im_architecture, "CS"),
        Course("Security policies & legal aspects", 5, 1, "", R.drawable.im_architecture, "CS"),
        Course("Technique of intrusion & defense", 5, 1, "", R.drawable.im_architecture, "CS"),
        Course("Ethical hacking", 5, 1, "", R.drawable.im_architecture, "CS")
    )

    private val preparatoryRecyclerViews: List<RecyclerView> by lazy {
        listOf(
            binding.includedLayoutPreparatoryCycle.rv1cpSemester1,
            binding.includedLayoutPreparatoryCycle.rv1cpSemester2,
            binding.includedLayoutPreparatoryCycle.rv2cpSemester1,
            binding.includedLayoutPreparatoryCycle.rv2cpSemester2
        )
    }

    private val superiorRecyclerViews: List<RecyclerView> by lazy {
        listOf(
            binding.includedLayoutSuperiorCycle.rv1csSemester1,
            binding.includedLayoutSuperiorCycle.rv1csSemester2,
            binding.includedLayoutSuperiorCycle.rv2csSemester1,
            binding.includedLayoutSuperiorCycle.rv2csSemester2Ai,
            binding.includedLayoutSuperiorCycle.rv2csSemester2Cs,
            binding.includedLayoutSuperiorCycle.rv3csSemester1Ai,
            binding.includedLayoutSuperiorCycle.rv3csSemester1Cs
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPreparatoryRvs()
        setupSuperiorRvs()
        setTabLayoutFunc()
        setupButtons()
    }

    private fun setTabLayoutFunc() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.viewFlipper.displayedChild = 0

                    }

                    1 -> {
                        binding.viewFlipper.displayedChild = 1
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })
    }

    private fun setupPreparatoryRvs() {
        preparatoryRecyclerViews.forEachIndexed { index, recyclerView ->
            when (index + 1) {
                1 -> {
                    setupRv(
                        recyclerView,
                        preparatoryCycleCourses.filter { course -> course.courseYear == 1 && course.courseSemester == 1 })
                }

                2 -> {
                    setupRv(
                        recyclerView,
                        preparatoryCycleCourses.filter { course -> course.courseYear == 1 && course.courseSemester == 2 })
                }

                3 -> {
                    setupRv(
                        recyclerView,
                        preparatoryCycleCourses.filter { course -> course.courseYear == 2 && course.courseSemester == 1 })
                }

                4 -> {
                    setupRv(
                        recyclerView,
                        preparatoryCycleCourses.filter { course -> course.courseYear == 2 && course.courseSemester == 2 })
                }
            }
        }
    }

    private fun setupSuperiorRvs() {
        superiorRecyclerViews.forEachIndexed { index, recyclerView ->
            when (index + 1) {
                1 -> {
                    setupRv(
                        recyclerView,
                        superiorCycleCourses.filter { course -> course.courseYear == 3 && course.courseSemester == 1 })
                }

                2 -> {
                    setupRv(
                        recyclerView,
                        superiorCycleCourses.filter { course -> course.courseYear == 3 && course.courseSemester == 2 })
                }

                3 -> {
                    setupRv(
                        recyclerView,
                        superiorCycleCourses.filter { course -> course.courseYear == 4 && course.courseSemester == 1 })
                }

                4 -> {
                    setupRv(
                        recyclerView,
                        superiorCycleCourses.filter { course -> course.courseYear == 4 && course.courseSemester == 2 && course.specialization == "AI" })
                }

                5 -> {
                    setupRv(
                        recyclerView,
                        superiorCycleCourses.filter { course -> course.courseYear == 4 && course.courseSemester == 2 && course.specialization == "CS" })
                }

//                6 -> {
//                    setupRv(
//                        recyclerView,
//                        superiorCycleCourses.filter { course -> course.courseYear == 5 && course.courseSemester == 1 && course.specialization == "AI" })
//                }
//
//                7 -> {
//                    setupRv(
//                        recyclerView,
//                        superiorCycleCourses.filter { course -> course.courseYear == 5 && course.courseSemester == 1 && course.specialization == "CS" })
//                }
            }
        }
    }

    private fun setupRv(recyclerView: RecyclerView, courseList: List<Course>) {
        mAdapter = CourseAdapter(requireContext(), courseList)
        mAdapter.setOnCourseClickedListener(this@LibraryFragment)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = mAdapter
    }

    private fun setupButtons() {
        binding.apply {
            includedLayoutPreparatoryCycle.apply {
                btn1cp1semester.apply {
                    setOnClickListener {
                        updateView(rv1cpSemester1, CP1S1)
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            if (showState[CP1S1]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
                        )
                    }
                }
                btn1cp2semester.apply {
                    setOnClickListener {
                        updateView(rv1cpSemester2, CP1S2)
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            if (showState[CP1S2]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
                        )
                    }
                }
                btn2cp1semester.apply {
                    setOnClickListener {
                        updateView(rv2cpSemester1, CP2S1)
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            if (showState[CP2S1]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
                        )
                    }
                }
                btn2cp2semester.apply {
                    setOnClickListener {
                        updateView(rv2cpSemester2, CP2S2)
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            if (showState[CP2S2]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
                        )
                    }
                }
            }
            includedLayoutSuperiorCycle.apply {
                btn1cs1semester.apply {
                    setOnClickListener {
                        updateView(rv1csSemester1, CS1S1)
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            if (showState[CP1S1]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
                        )
                    }
                }
                btn1cs2semester.apply {
                    setOnClickListener {
                        updateView(rv1csSemester2, CS1S2)
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            if (showState[CP1S2]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
                        )
                    }
                }
                btn2cs1semester.apply {
                    setOnClickListener {
                        updateView(rv2csSemester1, CS2S1)
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            if (showState[CP2S1]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
                        )
                    }
                }
                btn2cs2semesterAi.apply {
                    setOnClickListener {
                        updateView(rv2csSemester2Ai, CS2S2AI)
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            if (showState[CP2S2]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
                        )
                    }
                }
                btn2cs2semesterCs.apply {
                    setOnClickListener {
                        updateView(rv2csSemester2Cs, CS2S2CS)
                        icon = ContextCompat.getDrawable(
                            requireContext(),
                            if (showState[CP2S2]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
                        )
                    }
                }
                btn3cs1semesterAi.apply {
                    setOnClickListener {
                        Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
//                        updateView(rv3csSemester1Ai, CS3S1AI)
//                        icon = ContextCompat.getDrawable(
//                            requireContext(),
//                            if (showState[CP2S2]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
//                        )
                    }
                }
                btn3cs1semesterCs.apply {
                    setOnClickListener {
                        Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
//                        updateView(rv3csSemester1Cs, CS3S1CS)
//                        icon = ContextCompat.getDrawable(
//                            requireContext(),
//                            if (showState[CP2S2]!!) R.drawable.icon_drop_down_arrow else R.drawable.icon_arrow_drop_up
//                        )
                    }
                }
            }
        }
    }
    private fun updateView(recyclerView: RecyclerView, showStateKey: String) {
        val isShowing = showState[showStateKey]!!
        if (isShowing) {
            rvSlideAnimator.collapseView(recyclerView)
            showState[showStateKey] = false
        } else {
            rvSlideAnimator.expandView(recyclerView)
            showState[showStateKey] = true
        }
    }
    override fun onCourseClicked(driveFolderId: String, driveFolderName: String) {
        if (driveFolderId.isBlank()){
            Toast.makeText(requireContext(), "Not available at the moment", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(requireContext(), FilesPreviewActivity::class.java)
        intent.putExtra(Constants.DRIVE_FOLDER_ID, driveFolderId)
        intent.putExtra(Constants.DRIVE_FOLDER_NAME, driveFolderName)
        startActivity(intent)
    }


}