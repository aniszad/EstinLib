package com.az.elib.presentation.adapters// CourseAdapter.kt
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.az.elib.databinding.LayoutCourseViewBinding
import com.az.elib.domain.interfaces.OnCourseClickedListener
import com.az.elib.domain.models.Course

class CourseAdapter(private val context: Context, private val courseList: List<Course>) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    private lateinit var onCourseClickedListener: OnCourseClickedListener
    inner class CourseViewHolder(private val binding: LayoutCourseViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            binding.tvCourseTitle.text = course.courseName
            binding.imCourse.setImageDrawable(ContextCompat.getDrawable(context, course.courseImage))
            binding.tvCourseTitle.text = course.courseName
            binding.cardViewCourse.setOnClickListener {
                if(::onCourseClickedListener.isInitialized) onCourseClickedListener.onCourseClicked(course.courseDriveFileId, course.courseName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = LayoutCourseViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courseList[position])
    }

    override fun getItemCount(): Int {
        return courseList.size
    }
    fun setOnCourseClickedListener(onCourseClickedListener: OnCourseClickedListener) {
        this.onCourseClickedListener = onCourseClickedListener
    }
}
