using System.Data;
using Npgsql;

namespace OfficeFinderBackend.Repositories
{
   public class BaseRepository
   {
      private const string ConnectionString = "Server=127.0.0.1;User Id=postgres;Password=test;Database=gis;";
      protected IDbConnection Connection;
      public BaseRepository()
      {
         Connection = new NpgsqlConnection(ConnectionString);
      }

   }
}