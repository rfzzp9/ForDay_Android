package com.forday.app.presentation.main

import androidx.navigation3.runtime.NavKey
import com.forday.app.core.designsystem.component.navigationbar.BottomBarTab
import com.forday.app.core.navigation.MyPage
import com.forday.app.presentation.discovery.navigation.Discovery
import com.forday.app.presentation.home.navigation.Home
import com.forday.app.presentation.onboarding.login.navigation.Login
import com.forday.app.presentation.story.navigation.Story
import timber.log.Timber

/**
 * 네비게이션 로직을 처리하는 클래스
 */
class Navigator(val state: MainNavigationState) {

    /**
     * 주어진 route로 이동합니다.
     *
     * - route가 Top-level destination이면 → 탭 전환
     * - 그렇지 않으면 → 현재 탭의 백스택에 추가
     */
    fun navigate(route: NavKey) {
        val currentTop = state.topLevelRoute
        val currentStackBefore = state.backStacks[currentTop]
        Timber.e(
            "Navigator.navigate(route=$route) currentTop=$currentTop stackSizeBefore=${currentStackBefore?.size} lastBefore=${currentStackBefore?.lastOrNull()}"
        )
        if (route in state.backStacks.keys) {
            // 탭 전환
            state.topLevelRoute = route
        } else {
            // startRoute가 Login/온보딩처럼 Top-level이 아닌 경우,
            // 현재 topLevelRoute가 Home 같은 탭으로 잡혀있더라도 startRoute 스택에 화면을 추가해야
            // 실제 표시되고 있는 플로우(Login/온보딩)가 정상적으로 전환됩니다.
            val activeStackKey =
                if (state.startRoute !in TOP_LEVEL_DESTINATIONS.keys && state.topLevelRoute in TOP_LEVEL_DESTINATIONS.keys) {
                    state.startRoute
                } else {
                    state.topLevelRoute
                }

            state.backStacks[activeStackKey]?.add(route)
        }

        state.notifyNavChanged()

        val currentTopAfter = state.topLevelRoute
        val currentStackAfter = state.backStacks[currentTopAfter]
        Timber.e(
            "Navigator.navigate(route=$route) currentTopAfter=$currentTopAfter stackSizeAfter=${currentStackAfter?.size} lastAfter=${currentStackAfter?.lastOrNull()}"
        )
    }

    /**
     * 뒤로가기 처리
     *
     * - 현재 화면이 탭의 첫 화면이면 → 시작 탭으로 이동
     * - 그렇지 않으면 → 백스택에서 pop
     */
    fun goBack() {
        handleBack()
    }

    /**
     * 뒤로가기를 실제로 처리했는지 반환합니다.
     *
     * @return true면 네비게이션이 처리되어 소비됨, false면 더 이상 처리할 게 없음(예: Home 루트)
     */
    fun handleBack(): Boolean {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Back stack for ${state.topLevelRoute} doesn't exist")

        val currentRoute = currentStack.last()

        return if (currentRoute == state.topLevelRoute) {
            // 탭의 첫 화면이면 시작 탭으로 이동
            // 단, Login 화면에서는 뒤로가기로 이전 화면 복귀가 불가능해야 함
            if (state.topLevelRoute == Login) {
                true
            } else {
                // startRoute가 탭(Home/Discovery/Story/MyPage)인 경우에만 startRoute로 이동
                // startRoute가 Login/온보딩이면 여기서 더 이상 처리하지 않음(상위에서 앱 종료 UX 처리)
                if (state.startRoute in TOP_LEVEL_DESTINATIONS.keys) {
                    // 이미 startRoute와 현재 탭이 동일하면 더 이상 이동할 곳이 없으므로 처리하지 않았다고 반환
                    if (state.startRoute == state.topLevelRoute) {
                        return false
                    }
                    state.topLevelRoute = state.startRoute
                    state.notifyNavChanged()
                    true
                } else {
                    false
                }
            }
        } else {
            // 백스택에서 제거
            currentStack.removeLastOrNull()
            state.notifyNavChanged()
            true
        }
    }

    /**
     * 모든 백스택을 초기화한 뒤, 지정 route로 이동합니다.
     * 뒤로가기를 눌러도 이전 화면으로 돌아가지 않도록 하기 위해 사용합니다.
     */
    fun resetTo(route: NavKey) {
        state.backStacks.forEach { (key, stack) ->
            while (stack.removeLastOrNull() != null) { }
            stack.add(key)
        }

        state.topLevelRoute = route
        state.startRoute = route  // ← startRoute도 갱신 (val → var로 변경 필요)

        val targetStack = state.backStacks[route]
            ?: error("Back stack for $route doesn't exist")

        if (targetStack.lastOrNull() != route) {
            targetStack.add(route)
        }

        state.notifyNavChanged()
    }

    /**
     * 현재 route를 다른 route로 교체합니다.
     * (LoadingRoutines → RoutineAiRecommend 같은 경우에 사용)
     */
    fun replaceWith(route: NavKey) {
        val currentStack = state.backStacks[state.topLevelRoute]
            ?: error("Back stack for ${state.topLevelRoute} doesn't exist")

        currentStack.removeLastOrNull()
        currentStack.add(route)

        state.notifyNavChanged()
    }
}

/**
 * 하단 네비게이션 바의 아이템 데이터
 */
data class BottomNavItem(
    val icon: Int,  // Drawable resource ID
    val iconSelected: Int,  // Drawable resource ID (선택 시)
    val title: String,
)

/**
 * Top-level destinations (하단 네비게이션 바의 탭들)
 */
val TOP_LEVEL_DESTINATIONS = mapOf(
    Home to BottomNavItem(
        icon = com.dayn.forday.R.drawable.home,
        iconSelected = com.dayn.forday.R.drawable.home_selected,
        title = "홈"
    ),
    Discovery to BottomNavItem(
        icon = com.dayn.forday.R.drawable.discovery,
        iconSelected = com.dayn.forday.R.drawable.discovery_selected,
        title = "발견"
    ),
    Story to BottomNavItem(
        icon = com.dayn.forday.R.drawable.ic_story_unselected,
        iconSelected = com.dayn.forday.R.drawable.ic_story_unselected,  //selected로 수정해야 함
        title = "소식"
    ),
    MyPage to BottomNavItem(
        icon = com.dayn.forday.R.drawable.ic_my,
        iconSelected = com.dayn.forday.R.drawable.ic_my_selected,
        title = "마이"
    ),
)

/**
 * NavKey를 BottomBarTab으로 변환
 */
fun NavKey.toBottomBarTab(): BottomBarTab = when(this) {
    Home -> BottomBarTab.HOME
    Discovery -> BottomBarTab.DISCOVERY
    Story -> BottomBarTab.STORY
    MyPage -> BottomBarTab.MYPAGE
    else -> BottomBarTab.HOME
}

/**
 * BottomBarTab을 NavKey로 변환
 */
fun BottomBarTab.toNavKey(): NavKey = when(this) {
    BottomBarTab.HOME -> Home
    BottomBarTab.DISCOVERY -> Discovery
    BottomBarTab.STORY -> Story
    BottomBarTab.MYPAGE -> MyPage
}