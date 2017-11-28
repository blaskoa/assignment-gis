using System.Collections.Generic;

namespace OfficeFinderBackend.Models
{
   public class Parking
   {
      public long Id { get; set; }
      public string Name { get; set; }
      public string GeoJson { get; set; }
      public IEnumerable<GeoPointString> GeoPointStrings { get; set; }
      public string Access { get; set; }
      public double Distance { get; set; }
      public double Score { get; set; }
   }
}