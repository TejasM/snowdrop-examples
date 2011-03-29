package org.jboss.snowdrop.samples.sportsclub.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.jboss.snowdrop.samples.sportsclub.utils.DateUtils;

/**
 * @author <a href="mailto:mariusb@redhat.com">Marius Bogoevici</a>
 */
@Entity
public class Account implements Serializable
{
   @Id
   @GeneratedValue
   private Long id;

   @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
   private Person subscriber;

   @Embedded
   private Balance balance;

   private Date creationDate;

   @ManyToOne
   private Membership membership;

   private BillingType billingType;

   private boolean closed;

   private Date closeDate;


   public Account()
   {
      this.balance = new Balance();
      this.balance.setAmount(BigDecimal.ZERO);
   }

   public Long getId()
   {
      return id;
   }

   public Person getSubscriber()
   {
      return subscriber;
   }

   public void setSubscriber(Person subscriber)
   {
      this.subscriber = subscriber;
      subscriber.setAccount(this);
   }

   public BillingType getBillingType()
   {
      return billingType;
   }

   public void setBillingType(BillingType billingType)
   {
      this.billingType = billingType;
   }

   public Date getCreationDate()
   {
      return creationDate;
   }

   public void setCreationDate(Date creationDate)
   {
      this.creationDate = DateUtils.normalizeDate(creationDate, TimeInterval.TIME_ZONE);
   }

   public TimeInterval getBillingPeriodFor(Date date)
   {
      Date normalizedDate = DateUtils.normalizeDate(date, TimeInterval.TIME_ZONE);
      Calendar calendar = new GregorianCalendar(TimeInterval.TIME_ZONE);
      calendar.setTime(normalizedDate);
      TimeInterval timeInterval = new TimeInterval();
      switch (billingType)
      {
         case MONTHLY:
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            timeInterval.setStartDate(calendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            timeInterval.setEndDate(calendar.getTime());
            break;
         case WEEKLY:
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            timeInterval.setStartDate(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 6);
            timeInterval.setEndDate(calendar.getTime());
            break;
         case BIWEEKLY:
            long duration = normalizedDate.getTime() - getCreationDate().getTime();
            long intervals = duration / TimeInterval.TWO_WEEKS;
            calendar.setTime(getCreationDate());
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.DAY_OF_MONTH, (int)intervals * 14);
            timeInterval.setStartDate(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 13);
            timeInterval.setEndDate(calendar.getTime());
            break;
         default:
            throw new IllegalArgumentException("Invalid BillingType value for account:" + billingType);
      }
     return timeInterval;
   }

   public Membership getMembership()
   {
      return membership;
   }

   public void setMembership(Membership membership)
   {
      this.membership = membership;
   }

   public boolean isClosed()
   {
      return closed;
   }

   public void setClosed(boolean closed)
   {
      this.closed = closed;
   }

   public Date getCloseDate()
   {
      return closeDate;
   }

   public void setCloseDate(Date closeDate)
   {
      this.closeDate = DateUtils.normalizeDate(closeDate, TimeInterval.TIME_ZONE);
   }

   public BigDecimal getFeePerBillingPeriod()
   {
      return membership.getAnnualFee().divide(BigDecimal.valueOf(billingType.periodsPerYear()), 2, RoundingMode.CEILING);
   }

   public Balance getBalance()
   {
      return balance;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      Account that = (Account) o;

      if (id != null ? !id.equals(that.id) : that.id != null)
      {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode()
   {
      return id != null ? id.hashCode() : 0;
   }
}

