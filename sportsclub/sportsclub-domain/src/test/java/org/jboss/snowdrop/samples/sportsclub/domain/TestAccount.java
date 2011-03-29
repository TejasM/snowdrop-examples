package org.jboss.snowdrop.samples.sportsclub.domain;

import static org.jboss.snowdrop.samples.sportsclub.domain.entity.TimeInterval.DAY;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.snowdrop.samples.sportsclub.domain.entity.Account;
import org.jboss.snowdrop.samples.sportsclub.domain.entity.BillingType;
import org.jboss.snowdrop.samples.sportsclub.domain.entity.Membership;
import org.jboss.snowdrop.samples.sportsclub.domain.entity.Name;
import org.jboss.snowdrop.samples.sportsclub.domain.entity.Person;
import org.jboss.snowdrop.samples.sportsclub.domain.entity.TimeInterval;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Marius Bogoevici
 */
public class TestAccount
{

   @Test
   public void testTimeInterval()
   {
      Date currentDate = new Date();

      TimeInterval ti1 = new TimeInterval();
      ti1.setStartDate(new Date(currentDate.getTime() - 1 * DAY));
      ti1.setEndDate(new Date(currentDate.getTime() + 1 * DAY));
      Assert.assertTrue(ti1.contains(currentDate));

      TimeInterval ti2 = new TimeInterval();
      ti2.setStartDate(new Date(currentDate.getTime()));
      ti2.setEndDate(new Date(currentDate.getTime() + 1 * DAY));
      Assert.assertTrue(ti2.contains(currentDate));

      TimeInterval ti3 = new TimeInterval();
      ti3.setStartDate(new Date(currentDate.getTime() - 1 * DAY));
      ti3.setEndDate(new Date(currentDate.getTime()));
      Assert.assertTrue(ti3.contains(currentDate));

      TimeInterval ti4 = new TimeInterval();
      ti4.setStartDate(new Date(currentDate.getTime()));
      ti4.setEndDate(new Date(currentDate.getTime()));
      Assert.assertTrue(ti4.contains(currentDate));

      TimeInterval ti5 = new TimeInterval();
      ti5.setStartDate(new Date(currentDate.getTime() + 1 * DAY));
      ti5.setEndDate(new Date(currentDate.getTime() + 2 * DAY));
      Assert.assertTrue(!ti5.contains(currentDate));

      TimeInterval ti6 = new TimeInterval();
      ti6.setStartDate(new Date(currentDate.getTime() - 2 * DAY));
      ti6.setEndDate(new Date(currentDate.getTime() - 1 * DAY));
      Assert.assertTrue(!ti6.contains(currentDate));
   }

   @Test
   public void testMonthly()
   {
      Account account = createAccount(BillingType.MONTHLY, BigDecimal.valueOf(120l));

      Date currentDate = new Date();
      final TimeInterval timeInterval = account.getBillingPeriodFor(currentDate);
      Assert.assertTrue(timeInterval.contains(currentDate));
   }

   @Test
   public void testWeekly()
   {
      Account account = createAccount(BillingType.WEEKLY, BigDecimal.valueOf(520l));

      Date currentDate = new Date();
      final TimeInterval timeInterval = account.getBillingPeriodFor(currentDate);
      Assert.assertTrue(timeInterval.contains(currentDate));

   }

   @Test
   public void testBiweekly()
   {
      Account account = createAccount(BillingType.BIWEEKLY, BigDecimal.valueOf(260l));

      Date currentDate = new Date();
      final TimeInterval timeInterval = account.getBillingPeriodFor(currentDate);
      Assert.assertTrue(timeInterval.contains(currentDate));
   }

   @Test
   public void testBiweeklyOnSunday() throws ParseException
   {
      Account account = createAccount(BillingType.BIWEEKLY, BigDecimal.valueOf(260l));
      SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");


      Date sundayDate = formatter.parse("27-03-2011 16:25");
      final TimeInterval timeInterval = account.getBillingPeriodFor(sundayDate);
      Assert.assertTrue(timeInterval.contains(sundayDate));
   }

   @Test
   public void testBiweeklyOnSundayEarlyHours() throws ParseException
   {
      Account account = createAccount(BillingType.BIWEEKLY, BigDecimal.valueOf(260l));
      SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");


      Date sundayDate = formatter.parse("27-03-2011 6:25");
      final TimeInterval timeInterval = account.getBillingPeriodFor(sundayDate);
      Assert.assertTrue(timeInterval.contains(sundayDate));
   }


   private Account createAccount(BillingType billingType, BigDecimal amount)
   {
      Account account = new Account();
      account.setBillingType(billingType);
      account.setClosed(false);
      account.setCreationDate(new Date());
      Membership membership = new Membership("GOLD");
      membership.setActive(true);
      membership.setAnnualFee(amount);
      account.setMembership(membership);
      account.setSubscriber(new Person());
      account.getSubscriber().setName(new Name());
      account.getSubscriber().getName().setFirstName("Samuel");
      account.getSubscriber().getName().setLastName("Vimes");
      return account;
   }

}
