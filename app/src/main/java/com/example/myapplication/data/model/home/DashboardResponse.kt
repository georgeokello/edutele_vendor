package com.example.myapplication.data.model.home


data class DashboardResponse(
    val main_stats: MainStats,
    val quick_stats: QuickStats,
    val recent_activities: List<RecentRedemptions>,
    val top_branches: List<TopBranch>,
    val float_allocation: AccessAllocation
)

data class MainStats(
    val total_users: StatItem,
    val active_cards: StatItem,
    val owners: StatItem,
    val branches: StatItem,
    val printed_cards: StatItem,
    val last_card_number: String,
    val total_float: Double,
    val float_allocated: Double,
    val float_unallocated: Double,
    val total_purchase_volume: Double
)

data class QuickStats(
    val access_events_today: StatItem,
    val pending_access_allocations: StatItem,
    val pending_access_point_floats: StatItem,
    val success_rate: StatItem,
    val access_allocations_today: StatItem,
    val access_point_floats_today: StatItem,
    val revenue_today: StatItem,
    val tx_posted_today: Int,
    val tx_pending_today: Int
)

data class StatItem(
    val value: Double,
    val change_pct: Double
)

data class RecentRedemptions(
    val activity: String,
    val user: String,
    val date: String,
    val amount: String,
    val status: String,
    val type: String,
    val card_holder: String,
    val reference: String
)

data class TopBranch(
    val name: String,
    val revenue: String,
    val access_events: Int,
    val growth: String
)

data class AccessAllocation(
    val total_allocated: Double,
    val load_count: Int,
    val sales_processed: Int,
    val available_balance: Double
)