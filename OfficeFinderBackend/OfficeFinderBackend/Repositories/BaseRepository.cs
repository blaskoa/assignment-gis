using System.Data;
using Npgsql;

namespace OfficeFinderBackend.Repositories
{
   public class BaseRepository
   {
      private const string ConnectionString = "Server=192.168.0.220;User Id=postgres;Password=test;Database=gis;";
      private const string SelectTemplate =
         @"WITH actual_select AS (
             {0}
         )
         SELECT jsonb_build_object(
             'type',       'Feature',
             'id',         osm_id,
             'geometry',   ST_AsGeoJSON(way)::jsonb,
             'properties', to_jsonb(row) - 'osm_id' - 'way'
         ) FROM actual_select row;";
      protected IDbConnection Connection;
      public BaseRepository()
      {
         Connection = new NpgsqlConnection(ConnectionString);
      }

      protected string GetQuery(string actualQuery)
      {
         return string.Format(SelectTemplate, actualQuery);
      }

   }
}