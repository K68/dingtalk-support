package com.amzport.monix

import com.amzport.monix.SubscribeMatchType.SubscribeMatchType
import monix.execution.Ack.{Continue, Stop}
import monix.execution.{Ack, Cancelable}
import monix.execution.cancelables.SingleAssignCancelable
import monix.reactive.{Observable, Observer}
import monix.reactive.OverflowStrategy.Unbounded
import monix.reactive.observers.Subscriber
import monix.reactive.subjects.PublishSubject
import monix.execution.Scheduler.{global => scheduler}

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, FiniteDuration}

object SubscribeMatchType extends Enumeration {
  type SubscribeMatchType = Value
  val ExactMatch, // 完全匹配
  // 订阅的消息名 大于 通知的消息名
  PrefixNotify, SuffixNotify, ContainsNotify,
  // 通知的消息名 大于 订阅的消息名
  PrefixSubscri, SuffixSubscri, ContainsSubscri = Value
}

object ObserverManager {

  trait OMEvent {
    def event: String = "observer_manager_event"
    def allowMatchTypes: Set[SubscribeMatchType] = Set.empty
  }

  private val observable: Observable[OMEvent] = Observable.create[OMEvent](Unbounded) { subscriber =>
    val c = SingleAssignCancelable()
    subscriberOpt = Some(subscriber)
    c := Cancelable(() => {
      subscriber.onComplete()
      subscriberOpt = None
    })
  }

  private var subscriberOpt: Option[Subscriber.Sync[OMEvent]] = None
  private val publishSubject: PublishSubject[OMEvent] = PublishSubject[OMEvent]()
  private val globalSwitch = observable.subscribe(publishSubject)(scheduler)

  def shutdown(): Unit = {
    globalSwitch.cancel()
  }

  /**
    * @param event      Event Key
    * @param matchType  SubscribeMatchType
    * @param period     time that has to pass before emiting new items
    * @param n          maximum number of items emitted per given `period`;
    *                   -1 for throttleFirst;
    *                   -2 for throttleLast;
    *                   -3 for debounce (throttleWithTimeout);
    * @param cb         Callback
    * @return
    */
  def subscribe(event: String,
                matchType: SubscribeMatchType,
                period: FiniteDuration = Duration.Zero,
                n: Int = 0
               )(cb: OMEvent => Boolean): Cancelable = {
    val observer: Observer[OMEvent] = new Observer[OMEvent] {
      override def onNext(elem: OMEvent): Future[Ack] = {
        if (elem.allowMatchTypes.nonEmpty && !elem.allowMatchTypes.contains(matchType)) {
          Continue
        } else if (cb(elem)) {
          Continue
        } else {
          Stop
        }
      }
      override def onError(ex: Throwable): Unit = ex.printStackTrace()
      override def onComplete(): Unit = println(s"O completed: $event")
    }

    val _subjectMatch = matchType match {
      case SubscribeMatchType.ExactMatch =>
        publishSubject.filter(e => e.event == event)

      case SubscribeMatchType.PrefixNotify =>
        publishSubject.filter(e => event.startsWith(e.event))

      case SubscribeMatchType.SuffixNotify =>
        publishSubject.filter(e => event.endsWith(e.event))

      case SubscribeMatchType.ContainsNotify =>
        publishSubject.filter(e => event.contains(e.event))

      case SubscribeMatchType.PrefixSubscri =>
        publishSubject.filter(e => e.event.startsWith(event))

      case SubscribeMatchType.SuffixSubscri =>
        publishSubject.filter(e => e.event.endsWith(event))

      case SubscribeMatchType.ContainsSubscri =>
        publishSubject.filter(e => e.event.contains(event))

      case _ =>
        publishSubject.filter(e => e.event == event)
    }

    val _subjectThrottle = if (n == 0 || period == Duration.Zero) {
      _subjectMatch
    } else if (n == -1) { // throttleFirst
      _subjectMatch.throttleFirst(period)
    } else if (n == -2) { // throttleLast
      _subjectMatch.throttleLast(period)
    } else if (n == -3) { // debounce
      _subjectMatch.debounce(period)
    } else {
      val _n = math.max(n, 1)
      _subjectMatch.throttle(period, _n)
    }

    _subjectThrottle.subscribe(Subscriber(observer, scheduler))
  }

  def notify(event: OMEvent): Unit = {
    if (subscriberOpt.isDefined) {
      subscriberOpt.get.onNext(event)
    }
  }

}
