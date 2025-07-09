package com.example.layarnusantara.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import coil.compose.AsyncImage

import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator


@Composable
fun BannerView(modifier: Modifier = Modifier) {
    var bannerList by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    // Fetch banners from Firestore
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("data")
            .document("banners")
            .get()
            .addOnCompleteListener {
                bannerList = it.result.get("urls") as List<String>
            }
    }

    if (bannerList.isNotEmpty()) {
        val pagerState = rememberPagerState(initialPage = 0) {
            bannerList.size
        }

        // Auto-slide logic
        LaunchedEffect(pagerState.pageCount) {
            while (true) {
                kotlinx.coroutines.delay(3000) // Delay 3 detik
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }

        Column(modifier = modifier) {
            HorizontalPager(
                state = pagerState,
                pageSpacing = 24.dp
            ) { page ->
                AsyncImage(
                    model = bannerList[page],
                    contentDescription = "Banner image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            DotsIndicator(
                dotCount = bannerList.size,
                type = ShiftIndicatorType(
                    DotGraphic(
                        color = MaterialTheme.colorScheme.primary,
                        size = 6.dp
                    )
                ),
                pagerState = pagerState
            )
        }
    }
}






