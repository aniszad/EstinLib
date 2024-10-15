package com.az.elib.util

class ReactionCountFormatter {
    companion object {
        fun formatReactionCount(reactionCount: Long): String {
            return when {
                reactionCount < 1000 -> {
                    "$reactionCount likes"
                }
                reactionCount < 1000000 -> {
                    val count = reactionCount / 1000
                    "${count}K likes"
                }
                else -> {
                    val count = reactionCount / 1000000
                    "${count}M likes"
                }
            }
        }

        fun increment(toString: String): CharSequence? {
            return when {
                toString.contains("K") -> {
                    val count = toString.substringBefore("K").toInt()
                    "${count + 1}K likes"
                }
                toString.contains("M") -> {
                    val count = toString.substringBefore("M").toInt()
                    "${count + 1}M likes"
                }
                else -> {
                    val count = toString.substringBefore(" ").toInt()
                    "${count + 1} likes"
                }
            }

        }

        fun decrement(toString: String): CharSequence? {
            return when {
                toString.contains("K") -> {
                    val count = toString.substringBefore("K").toInt()
                    "${count - 1}K likes"
                }
                toString.contains("M") -> {
                    val count = toString.substringBefore("M").toInt()
                    "${count - 1}M likes"
                }
                else -> {
                    val count = toString.substringBefore(" ").toInt()
                    "${count - 1} likes"
                }
            }
        }
    }
}