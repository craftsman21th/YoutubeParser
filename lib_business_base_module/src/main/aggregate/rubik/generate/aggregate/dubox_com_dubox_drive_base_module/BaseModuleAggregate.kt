package rubik.generate.aggregate.dubox_com_dubox_drive_base_module

import androidx.annotation.Keep
import com.rubik.annotations.source.RAggregate
import com.rubik.annotations.source.RGenerated
import com.rubik.context.Aggregatable
import com.rubik.context.AggregateFactory
import com.rubik.identity.RAggregateId
import com.rubik.route.mapping.caseToTypeOfT
import com.rubik.route.mapping.toTypeOfT
import kotlin.Array
import kotlin.Function0
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import rubik.generate.context.dubox_com_dubox_drive_base_module.BaseModuleRouteActions
import com.rubik.activity.Launcher as RubikLauncher
import com.rubik.route.Queries as RubikQueries
import com.rubik.route.Result as RubikResult
import com.rubik.route.ResultGroups as RubikResultGroups
import com.rubik.router.uri.Path as RubikPath

/**
 * aggregate router function and router event of Rubik Context.
 *
 * context uri: [dubox://com.dubox.drive.base_module]
 * version: 1.1.0.3-AUTO
 */
@RGenerated(
  kind = "aggregate",
  by = "rubik-kapt:1.9.3.2-K1_5",
  version = "1.1.0.3-AUTO"
)
@Keep
class BaseModuleAggregate : Aggregatable, BaseModuleRouteActions {
  override fun onEvent(msg: String, queries: RubikQueries) {
    when(msg){
      else -> {}
    }
  }

  override fun onRoute(
    path: String,
    queries: RubikQueries,
    results: RubikResultGroups
  ) {
    when {
      "stats/multi/fields/update/count" == path ->  {
        val op = queries.value(0, null)
        val other = queries.value(1, null)
        statsMultiFieldsUpdateCount(
              op.caseToTypeOfT(),
              other.caseToTypeOfT()
            )
      }
      "stat/key/ignore/hot/open/ad/pv" == path ->  {
        statKeyIgnoreHotOpenAdPv().apply {
          results.set(0, RubikResult(this))
        }
      }
      "stat/show/permission/request/dialog" == path ->  {
        val mType = queries.value(0, null)
        statShowPermissionRequestDialog(
              mType.caseToTypeOfT()
            )
      }
      "stat/on/request/dialog/ok/clicked" == path ->  {
        val mType = queries.value(0, null)
        statOnRequestDialogOkClicked(
              mType.caseToTypeOfT()
            )
      }
      "stat/on/result/dialog/ok/clicked" == path ->  {
        val mType = queries.value(0, null)
        statOnResultDialogOkClicked(
              mType.caseToTypeOfT()
            )
      }
      "stat/on/result/dialog/cancel/clicked" == path ->  {
        val mType = queries.value(0, null)
        statOnResultDialogCancelClicked(
              mType.caseToTypeOfT()
            )
      }
      "stat/show/permission/result/dialog" == path ->  {
        val mType = queries.value(0, null)
        statShowPermissionResultDialog(
              mType.caseToTypeOfT()
            )
      }
      "stat/success/add/doze/white/list" == path ->  {
        statSuccessAddDozeWhiteList()
      }
      else -> { throw com.rubik.route.exception.BadPathOrVersionException(path)}
    }
  }

  override fun statsMultiFieldsUpdateCount(op: String, other: Array<String>?) {
    // com.dubox.drive.basemodule.component.ApisKt.statsMultiFieldsUpdateCount
    // - parameters:
    // --- op : kotlin.String
    // --- other : kotlin.Array<kotlin.String>?
    // - resultType:
    // --- null
    com.dubox.drive.basemodule.component.statsMultiFieldsUpdateCount(
          op,
          other
        )
  }

  override fun statKeyIgnoreHotOpenAdPv(): String? {
    // com.dubox.drive.basemodule.component.PermissionBaseApisCodeReviewKt.statKeyIgnoreHotOpenAdPV
    // - resultType:
    // --- kotlin.String
    return com.dubox.drive.basemodule.component.statKeyIgnoreHotOpenAdPV()
  }

  override fun statShowPermissionRequestDialog(mType: Int) {
    // com.dubox.drive.basemodule.component.PermissionBaseApisCodeReviewKt.statShowPermissionRequestDialog
    // - parameters:
    // --- mType : kotlin.Int
    // - resultType:
    // --- null
    com.dubox.drive.basemodule.component.statShowPermissionRequestDialog(
          mType
        )
  }

  override fun statOnRequestDialogOkClicked(mType: Int) {
    // com.dubox.drive.basemodule.component.PermissionBaseApisCodeReviewKt.statOnRequestDialogOkClicked
    // - parameters:
    // --- mType : kotlin.Int
    // - resultType:
    // --- null
    com.dubox.drive.basemodule.component.statOnRequestDialogOkClicked(
          mType
        )
  }

  override fun statOnResultDialogOkClicked(mType: Int) {
    // com.dubox.drive.basemodule.component.PermissionBaseApisCodeReviewKt.statOnResultDialogOkClicked
    // - parameters:
    // --- mType : kotlin.Int
    // - resultType:
    // --- null
    com.dubox.drive.basemodule.component.statOnResultDialogOkClicked(
          mType
        )
  }

  override fun statOnResultDialogCancelClicked(mType: Int) {
    // com.dubox.drive.basemodule.component.PermissionBaseApisCodeReviewKt.statOnResultDialogCancelClicked
    // - parameters:
    // --- mType : kotlin.Int
    // - resultType:
    // --- null
    com.dubox.drive.basemodule.component.statOnResultDialogCancelClicked(
          mType
        )
  }

  override fun statShowPermissionResultDialog(mType: Int) {
    // com.dubox.drive.basemodule.component.PermissionBaseApisCodeReviewKt.statShowPermissionResultDialog
    // - parameters:
    // --- mType : kotlin.Int
    // - resultType:
    // --- null
    com.dubox.drive.basemodule.component.statShowPermissionResultDialog(
          mType
        )
  }

  override fun statSuccessAddDozeWhiteList() {
    // com.dubox.drive.basemodule.component.PermissionBaseApisCodeReviewKt.statSuccessAddDozeWhiteList
    // - resultType:
    // --- null
    com.dubox.drive.basemodule.component.statSuccessAddDozeWhiteList()
  }

  @RGenerated(
    kind = "aggregate_companion",
    by = "rubik-kapt:1.9.3.2-K1_5",
    version = "1.1.0.3-AUTO"
  )
  @Keep
  companion object : AggregateFactory() {
    override val URI: String = "dubox://com.dubox.drive.base_module"

    override val EVENT_MSGS: List<String> = listOf()

    override val CREATOR: Function0<Aggregatable> = {BaseModuleAggregate()}
  }
}

/**
 * generated Rubik AggregateId.
 *
 * uri: [dubox://com.dubox.drive.base_module]
 * version: 1.1.0.3-AUTO
 */
@RGenerated(
  kind = "aggregate_Id",
  by = "rubik-kapt:1.9.3.2-K1_5",
  version = "1.1.0.3-AUTO"
)
@RAggregate(
  uri = "dubox://com.dubox.drive.base_module",
  version = "1.1.0.3-AUTO",
  token =
      "<CTX><API>stat/key/ignore/hot/open/ad/pv||false|false||<RSUT>kotlin.String</RSUT>|null|null|true</API>|<API>stat/on/request/dialog/ok/clicked||false|false|<QUER>kotlin.Int|false|false|null</QUER>|null|null|null|false</API>|<API>stat/on/result/dialog/cancel/clicked||false|false|<QUER>kotlin.Int|false|false|null</QUER>|null|null|null|false</API>|<API>stat/on/result/dialog/ok/clicked||false|false|<QUER>kotlin.Int|false|false|null</QUER>|null|null|null|false</API>|<API>stat/show/permission/request/dialog||false|false|<QUER>kotlin.Int|false|false|null</QUER>|null|null|null|false</API>|<API>stat/show/permission/result/dialog||false|false|<QUER>kotlin.Int|false|false|null</QUER>|null|null|null|false</API>|<API>stat/success/add/doze/white/list||false|false||null|null|null|false</API>|<API>stats/multi/fields/update/count||false|false|<QUER>kotlin.String|false|false|null</QUER>|<QUER>kotlin.Array<kotlin.String>?|false|false|null</QUER>|null|null|null|false</API>|||</CTX>"
)
@Keep
class BaseModuleAggregateId :
    RAggregateId(uri = "dubox://com.dubox.drive.base_module", version = "1.1.0.3-AUTO", token = "<CTX><API>stat/key/ignore/hot/open/ad/pv||false|false||<RSUT>kotlin.String</RSUT>|null|null|true</API>|<API>stat/on/request/dialog/ok/clicked||false|false|<QUER>kotlin.Int|false|false|null</QUER>|null|null|null|false</API>|<API>stat/on/result/dialog/cancel/clicked||false|false|<QUER>kotlin.Int|false|false|null</QUER>|null|null|null|false</API>|<API>stat/on/result/dialog/ok/clicked||false|false|<QUER>kotlin.Int|false|false|null</QUER>|null|null|null|false</API>|<API>stat/show/permission/request/dialog||false|false|<QUER>kotlin.Int|false|false|null</QUER>|null|null|null|false</API>|<API>stat/show/permission/result/dialog||false|false|<QUER>kotlin.Int|false|false|null</QUER>|null|null|null|false</API>|<API>stat/success/add/doze/white/list||false|false||null|null|null|false</API>|<API>stats/multi/fields/update/count||false|false|<QUER>kotlin.String|false|false|null</QUER>|<QUER>kotlin.Array<kotlin.String>?|false|false|null</QUER>|null|null|null|false</API>|||</CTX>")
