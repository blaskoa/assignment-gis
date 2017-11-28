using System.Collections.Generic;
using System.Linq;
using Dapper;
using GeoJSON.Net.Geometry;
using Newtonsoft.Json;
using OfficeFinderBackend.Models;

namespace OfficeFinderBackend.Repositories
{
   public class OfficeRepository : BaseRepository
   {
      private const string testQuery =
         @"SELECT ST_AsGeoJSON(way)::jsonb as geojson, name, osm_id as id
              FROM public.planet_osm_polygon
              where (building = 'office' OR landuse='commercial')
              order by name asc
              limit 100";

      private const string selectNearbyOffices =
         @"SELECT ST_AsGeoJSON(way)::jsonb as geojson, name, osm_id as id
              FROM public.planet_osm_polygon
              where (building = 'office' or landuse='commercial')
              and st_dwithin(way, st_setsrid(st_makepoint(@longitude, @latitude), 4326), 0.05)
              and name is not null
              order by st_distance(way, st_setsrid(st_makepoint(@longitude, @latitude), 4326)) asc
              limit 100";


      public IEnumerable<Office> GetOffice()
      {
         IEnumerable<Office> serializedResult = Connection.Query<Office>(testQuery).ToList();
         
         return serializedResult.Select(FillOffice);
      }

      public IEnumerable<Office> GetOfficesFiltered(double latitude, double longitude)
      {
         var parameters = new
         {
            latitude = latitude,
            longitude = longitude
         };
         
         IEnumerable<Office> serializedResult = Connection.Query<Office>(selectNearbyOffices, parameters).ToList();

         return serializedResult.Select(FillOffice);
         
      }
      private static Office FillOffice(Office office)
      {
         Polygon polygon = JsonConvert.DeserializeObject<Polygon>(office.GeoJson);

         office.GeoPointStrings = polygon.Coordinates.Select(x =>
         {
            return new GeoPointString
            {
               GeoPoints = x.Coordinates.Select(y => new GeoPoint
               {
                  Latitude = y.Latitude,
                  Longitude = y.Longitude
               })
            };
         });
         return office;
      }
   }
}