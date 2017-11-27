using System.Collections.Generic;
using Dapper;
using OfficeFinderBackend.Models;

namespace OfficeFinderBackend.Repositories
{
   public class OfficeRepository : BaseRepository
   {
      private const string testQuery =
         @"SELECT *
              FROM public.planet_osm_polygon
              where (building = 'office' OR landuse='commercial')
              order by name asc
              limit 100";

      private const string selectNearbyOffices =
         @"SELECT *
              FROM public.planet_osm_polygon
              where (building = 'office' or landuse='commercial')
              and st_dwithin(way, st_setsrid(st_makepoint(@longitude, @latitude), 4326), 5)
              order by name asc
              limit 100";

      public IEnumerable<string> GetOffice()
      {
         IEnumerable<string> serializedResult = Connection.Query<string>(GetQuery(testQuery));

         return serializedResult;
      }
      public IEnumerable<string> GetOfficesFiltered(double latitude, double longitude)
      {
         var parameters = new
         {
            latitude = latitude,
            longitude = longitude
         };

         IEnumerable<string> serializedResult = Connection.Query<string>(GetQuery(selectNearbyOffices), parameters);

         return serializedResult;
      }
   }
}