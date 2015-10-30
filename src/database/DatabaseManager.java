package database;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import data.Metadata;
import data.TrackingGroup;

/**
 * A class for queuing up jobs for the database which we don't want to block on.
 * Probably mostly saves.???????
 * 
 * @credit http://www.tutorialspoint.com/hibernate/hibernate_examples.htm
 */
public class DatabaseManager
{
	private static SessionFactory factory;

	static
	{
		try
		{
			factory = new Configuration().configure().buildSessionFactory();
		}
		catch (Throwable ex)
		{
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static void saveProductUUID(Metadata fileMetadata)
	{
		// TODO Auto-generated method stub

	}

	public static void saveFileHash(Metadata fileMetadata)
	{
		// TODO Auto-generated method stub

	}

	public static Integer addTrackingGroup(TrackingGroup group)
	{
		Session session = factory.openSession();
		Transaction transaction = null;
		Integer id = null;
		try
		{
			transaction = session.beginTransaction();
			hibernate.TrackingGroup group_pojo =
							new hibernate.TrackingGroup(group.getName());
			id = (Integer) session.save(group_pojo);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			if (transaction != null)
				transaction.rollback();
			e.printStackTrace();
		}
		finally
		{
			session.close();
		}
		return id;
	}

	public static boolean containsFileHash(byte[] hash, TrackingGroup group)
	{
		// TODO implement database of trackingGroup/fileHash/fragment1UUID
		// why uuids? because in the case of a metadata update, you might not
		// know what the previous fragment1UUID was if it was a metadata update
		// due to a path change. All you'd know is you've seen this hash before,
		// not where it was saved last.
		return false;
	}

}
